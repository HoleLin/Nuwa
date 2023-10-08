package cn.holelin.dicom.pacs_v1.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author HoleLin
 */
@Getter
public class PullTaskTimeOutCancelEvent extends ApplicationEvent {
    private final String taskId;

    public PullTaskTimeOutCancelEvent(String taskId) {
        super(new Object());
        this.taskId = taskId;
    }
}
