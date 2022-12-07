package cn.holelin.dicom.pacs.support;

import cn.holelin.dicom.pacs.enums.InformationModelEnum;
import cn.holelin.dicom.pacs.param.PacsMoveParam;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DimseRSPHandler;
import org.dcm4che3.net.Priority;
import org.dcm4che3.net.pdu.PresentationContext;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 17:28
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 17:28
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class PacsMoveSupport extends AbstractPacsOperationTemplate {

    /**
     * 接收DICOM文件的SCP的AET
     */
    private final String desAeTitle;

    private int cancelAfter;

    private boolean releaseEager;
    private ScheduledFuture<?> scheduledCancel;

    public PacsMoveSupport(PacsMoveParam param) {
        super(param);
        this.desAeTitle = param.getDesAeTitle();

    }
    @Override
    public void preConnect(InformationModelEnum model) {
        aarq.addPresentationContext(new PresentationContext(
                this.aarq.getNumberOfPresentationContexts() * 2 + 1, model.cuid,
                CT_IMAGE_STORAGE
        ));
    }

    @Override
    public void execute(InformationModelEnum model, Attributes conditions) throws IOException, InterruptedException {
        final DimseRSPHandler rspHandler = new DimseRSPHandler(this.association.nextMessageID()) {
            @Override
            public void onDimseRSP(Association as, Attributes cmd,
                                   Attributes data) {
                super.onDimseRSP(as, cmd, data);
            }
        };
        this.association.cmove(model.cuid, Priority.NORMAL, conditions, null, desAeTitle, rspHandler);
        if (cancelAfter > 0) {
            scheduledCancel = schedule(() -> {
                try {
                    rspHandler.cancel(association);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, cancelAfter, TimeUnit.MILLISECONDS);
        }
    }
    private ScheduledFuture<?> schedule(Runnable command, int delay, TimeUnit milliseconds) {
        if (this.scheduledExecutorService == null) {
            throw new IllegalStateException(
                    "scheduled executor service not initialized");
        }
        return scheduledExecutorService.schedule(command, delay, milliseconds);
    }

}
