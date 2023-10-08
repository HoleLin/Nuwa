package cn.holelin.dicom.pacs_v1.enums;

/**
 * @author HoleLin
 */
public enum PullTaskStateEnum {

    /**
     * 排队中
     */
    WAITING,

    /**
     * 处理中
     */
    PROCESSING,

    /**
     * 成功
     */
    SUCCEED,

    /**
     * 失败
     */
    FAILED

}
