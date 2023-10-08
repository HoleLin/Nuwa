package cn.holelin.dicom.pacs_v1.support;

import cn.holelin.dicom.pacs_v1.base.DefaultPacsBehaviorSupport;
import cn.holelin.dicom.pacs_v1.domain.PacsCMoveConfig;
import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.events.PullTaskFailedEvent;
import cn.holelin.dicom.pacs_v1.events.PullTaskProcessingEvent;
import cn.holelin.dicom.pacs_v1.events.PullTaskSucceedEvent;
import cn.holelin.dicom.pacs_v1.events.PullTaskTimeOutCancelEvent;
import cn.holelin.dicom.pacs_v1.request.PacsSearchRequest;
import cn.holelin.dicom.pacs_v1.response.OperationResponse;
import cn.holelin.dicom.pacs_v1.utils.PacsContext;
import cn.holelin.dicom.pacs_v1.utils.PacsHelper;
import cn.holelin.dicom.pacs_v1.utils.SpringUtil;
import cn.hutool.core.lang.Assert;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DimseRSPHandler;
import org.dcm4che3.net.Priority;
import org.dcm4che3.net.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * MOVE SCU实现类
 *
 * @author HoleLin
 */
public class PacsCMoveSupport extends DefaultPacsBehaviorSupport {

    private final Logger log = LoggerFactory.getLogger(PacsCMoveSupport.class);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * C-MOVE超时时间 单位分钟
     */
    private int cancelAfter = 30;

    /**
     * 任务是否已经结束
     */
    private volatile boolean finished = false;
    /**
     * 任务是否在处理中
     */
    private volatile boolean processing = false;

    private ScheduledFuture<?> scheduledCancel;
    private final String destinationAeTitle;

    public PacsCMoveSupport(PacsCMoveConfig config) {
        super(config);
        this.destinationAeTitle = config.getDestinationAeTitle();
    }

    public PacsCMoveSupport(PacsCMoveConfig config, Boolean needExtendedNegotiation) {
        super(config, needExtendedNegotiation);
        this.destinationAeTitle = config.getDestinationAeTitle();
    }

    @Override
    public OperationResponse doExecute(InformationModelEnum model, Attributes conditions) {
        Association association = getAssociation();
        Assert.isTrue(Objects.nonNull(association), "无法连接远端PACS服务,请检查远端PACS配置是否正常!");
        finished = false;
        OperationResponse response = new OperationResponse();
        CountDownLatch latch = new CountDownLatch(1);
        final CMoveDimseRSPHandler rspHandler = new CMoveDimseRSPHandler(association.nextMessageID(), latch);
        if (cancelAfter > 0) {
            scheduledCancel = schedule(() -> {
                try {
                    if (!finished) {
                        rspHandler.cancel(association);
                        latch.countDown();
                        processing = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, cancelAfter, TimeUnit.MINUTES);
        }
        try {
            association.cmove(model.getCuid(), Priority.NORMAL, conditions, null, destinationAeTitle, rspHandler);
            latch.await();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            disconnect();
            log.debug("返还连接....");
        }

        return response;
    }

    private ScheduledFuture<?> schedule(Runnable command, int delay, TimeUnit milliseconds) {
        return scheduledExecutorService.schedule(command, delay, milliseconds);
    }

    class CMoveDimseRSPHandler extends DimseRSPHandler {
        CountDownLatch latch;

        public CMoveDimseRSPHandler(int msgId, CountDownLatch latch) {
            super(msgId);
            this.latch = latch;

        }

        @Override
        public void onDimseRSP(Association as, Attributes cmd,
                               Attributes data) {
            super.onDimseRSP(as, cmd, data);
            int status = cmd.getInt(Tag.Status, -1);
            String taskId = PacsContext.peek();
            if (Status.isPending(status)) {
                log.info("NumberOfRemainingSuboperations:{},NumberOfCompletedSuboperations:{},NumberOfFailedSuboperations:{},NumberOfWarningSuboperations:{}",
                        cmd.getInt(Tag.NumberOfRemainingSuboperations, -1),
                        cmd.getInt(Tag.NumberOfCompletedSuboperations, -1),
                        cmd.getInt(Tag.NumberOfFailedSuboperations, -1),
                        cmd.getInt(Tag.NumberOfWarningSuboperations, -1)
                );
                if (!processing) {
                    SpringUtil.getContext().publishEvent(new PullTaskProcessingEvent(taskId));
                    finished = false;
                    processing = true;
                }
            } else if (Status.Success == status) {
                log.info("NumberOfCompletedSuboperations:{}",
                        cmd.getInt(Tag.NumberOfCompletedSuboperations, -1)
                );
                log.info("taskId:{},C-MOVE完成,发送通知", taskId);
                PacsContext.remove();
                latch.countDown();
                finished = true;
                processing = false;
                SpringUtil.getContext().publishEvent(new PullTaskSucceedEvent(taskId));
            } else {
                if (Status.Cancel == status) {
                    log.error("任务ID为:{},C-MOVE操作超时被取消", taskId);
                    SpringUtil.getContext().publishEvent(new PullTaskTimeOutCancelEvent(taskId));
                } else {
                    if (Status.MoveDestinationUnknown == status) {
                        log.error("目标AET未在数据源PACS上进行配置");
                    }
                    String errorComment = cmd.getString(Tag.ErrorComment, "");
                    log.error("status:{},ErrorComment:{}", status, errorComment);
                    SpringUtil.getContext().publishEvent(new PullTaskFailedEvent(taskId, "C-MOVE失败," + errorComment));
                }
                finished = true;
                processing = false;
                latch.countDown();
                PacsContext.remove();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PacsCMoveConfig pacsScuConfig = new PacsCMoveConfig();

        pacsScuConfig.setRemotePort(11112);
        pacsScuConfig.setRemoteHostName("192.168.11.216");
        pacsScuConfig.setRemoteAeTitle("DCM4CHEE");
        pacsScuConfig.setDestinationAeTitle("scp_test");

        String testFindScu = "test_move_scu";
        pacsScuConfig.setAeTitle(testFindScu);
        pacsScuConfig.setDeviceName(testFindScu);
        PacsCMoveSupport support = new PacsCMoveSupport(pacsScuConfig);

        System.out.println(support.echo());
        PacsSearchRequest request = new PacsSearchRequest();
        request.setStudyInstanceUid("1.2.840.113619.186.18420258241203197.20180813103736326.517");
        request.setSeriesInstanceUid("1.3.12.2.1107.5.8.15.102053.30000018081317495500100001914");
        support.execute(InformationModelEnum.MOVE, PacsHelper.buildFindAttributes(request));
        support.disconnect();
    }
}
