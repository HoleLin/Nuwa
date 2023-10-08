package cn.holelin.dicom.pacs_v1.consts;

/**
 * 字符串常量类
 * @author HoleLin
 */
public class StringConstants {


    /**
     * 用户拉图队列Redis Key前缀
     */
    public static final String USER_PULL_TASK_QUEUE_PREFIX_KEY = "pacs_pull_task:";
    /**
     * 是否有传输任务Redis Key 前缀
     */
    public static final String HAS_TASK_PREFIX_KEY = "pacs_has_task:";

    public static final String DEFAULT_PACS_SERVER_DEVICE_NAME = "YW_PACS_SERVER";

    public static final String USER_INFO = "userinfo";

}
