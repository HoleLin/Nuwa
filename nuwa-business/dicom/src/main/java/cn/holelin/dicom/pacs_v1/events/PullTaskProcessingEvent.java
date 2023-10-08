package cn.holelin.dicom.pacs_v1.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author HoleLin
 */
@Getter
public class PullTaskProcessingEvent extends ApplicationEvent {
    private final String taskId;

    public PullTaskProcessingEvent(String taskId) {
        super(new Object());
        this.taskId = taskId;
    }
}
