package cn.holelin.dicom.pacs_v1.manager;

import cn.holelin.dicom.pacs_v1.config.DefaultPacsServerProperties;
import cn.holelin.dicom.pacs_v1.config.PacsProperties;
import cn.holelin.dicom.pacs_v1.config.PacsServerProperties;
import cn.holelin.dicom.pacs_v1.domain.PacsCMoveConfig;
import cn.holelin.dicom.pacs_v1.entity.PullTaskRecord;
import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.request.PacsStoreCondition;
import cn.holelin.dicom.pacs_v1.support.PacsCMoveSupport;
import cn.holelin.dicom.pacs_v1.utils.GlobalPullTaskContext;
import cn.holelin.dicom.pacs_v1.utils.PacsContext;
import cn.holelin.dicom.pacs_v1.utils.PacsHelper;
import cn.hutool.json.JSONUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HoleLin
 */
@Slf4j
public class StoreDicomWithLockRunnable implements Runnable {

    private final PacsProperties pacsProperties;
    private final ReentrantLock lock = new ReentrantLock();
    private final PacsCMoveSupport support;

    public StoreDicomWithLockRunnable(PacsProperties pacsProperties) {
        this.pacsProperties = pacsProperties;
        this.support = new PacsCMoveSupport(config(),
                pacsProperties.getDefaultConfig().getNeedExtendedNegotiation());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (lock.tryLock()) {
                    PullTaskRecord task = GlobalPullTaskContext.poll();
                    if (Objects.nonNull(task)) {
                        PacsStoreCondition condition = task.getCondition();
                        String taskId = task.getTaskId();
                        log.info("开始处理任务:{},条件为:{}", taskId, JSONUtil.parseObj(condition).toString());
                        if (PacsContext.offer(taskId)) {
                            try {
                                support.execute(InformationModelEnum.MOVE, PacsHelper.buildBaseConditionAttributes(condition));
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error("处理C_MOVE异常,taskId:{}", taskId);
                            }
                        } else {
                            log.error("offer失败");
                        }
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    private PacsCMoveConfig config() {
        PacsServerProperties remoteConfig = pacsProperties.getRemoteConfig();
        DefaultPacsServerProperties defaultConfig = pacsProperties.getDefaultConfig();
        PacsCMoveConfig config = new PacsCMoveConfig();
        config.setRemotePort(remoteConfig.getPort());
        config.setRemoteHostName(remoteConfig.getHostname());
        config.setRemoteAeTitle(remoteConfig.getAet());
        config.setAeTitle(defaultConfig.getAet());
        config.setDeviceName(defaultConfig.getAet());
        config.setDestinationAeTitle(defaultConfig.getAet());
        return config;
    }
}
