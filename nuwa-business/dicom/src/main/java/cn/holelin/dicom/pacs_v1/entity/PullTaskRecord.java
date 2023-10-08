package cn.holelin.dicom.pacs_v1.entity;


import cn.holelin.dicom.pacs_v1.enums.PullTaskStateEnum;
import cn.holelin.dicom.pacs_v1.request.PacsStoreCondition;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author HoleLin
 */
@Data
//@Document(value = "pull_task_record")
public class PullTaskRecord {
    public static final String COLLECTION_NAME = "pull_task_record";
    public static final String STATE = "state";
    public static final String ID = "_id";
    public static final String ROLE_ID = "roleId";
    public static final String TASK_ID = "taskId";
    public static final String BATCH_ID = "batchId";
    public static final String FINISHED_TIME = "finishedTime";
    public static final String REASON = "reason";

//    @Id
    private String id;

    private String taskId;

    private String batchId;

    /**
     * 任务角色ID,后续上传数据库需要使用到
     */
    private String roleId;
    /**
     * 触发任务人的手机号,用于后续推送消息
     */
    private String telephone;

    private LocalDateTime createdTime = LocalDateTime.now();
    /**
     * 任务结束时间
     */
    private LocalDateTime finishedTime;

    /**
     * 任务的状态
     *
     * @see PullTaskStateEnum
     */
    private String state;
    /**
     * 失败原因,只有state状态为PullTaskStateEnum.FAILED时非null
     */
    private String reason;

    /**
     * 请求信息
     */
    private PacsStoreCondition condition;

}
