package cn.holelin.dicom.pacs_v1.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * @author HoleLin
 */
@Slf4j
@Component
public class PullTaskEventListener {




    /**
     * 拉图任务进行中事件监听器
     *
     * @param event 拉图任务进行中事件
     */
    @EventListener({PullTaskProcessingEvent.class})
    public void processingListener(PullTaskProcessingEvent event) {
        // 更新任务状态为处理中
    }

    /**
     * 拉图任务成功事件监听器
     *
     * @param event 拉图任务成功事件
     */
    @EventListener({PullTaskSucceedEvent.class})
    public void succeedListener(PullTaskSucceedEvent event) {
        // 更新任务状态为处理成功

    }

    /**
     * 拉图任务失败事件监听器
     *
     * @param event 拉图任务失败事件
     */
    @EventListener({PullTaskFailedEvent.class})
    public void failedListener(PullTaskFailedEvent event) {
        // 更新任务状态为处理失败

    }

    /**
     * 拉图任务失败事件监听器
     *
     * @param event 拉图任务失败事件
     */
    @EventListener({PullTaskTimeOutCancelEvent.class})
    public void timeOutCancelListener(PullTaskTimeOutCancelEvent event) {
        // 更新任务状态为处理失败
    }

}
