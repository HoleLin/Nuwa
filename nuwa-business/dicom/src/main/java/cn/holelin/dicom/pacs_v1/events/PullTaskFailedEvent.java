package cn.holelin.dicom.pacs_v1.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author HoleLin
 */
@Getter
public class PullTaskFailedEvent extends ApplicationEvent {
    private final String taskId;
    private final String reason;

    public PullTaskFailedEvent(String taskId,String reason) {
        super(new Object());
        this.taskId = taskId;
        this.reason = reason;
    }
}
