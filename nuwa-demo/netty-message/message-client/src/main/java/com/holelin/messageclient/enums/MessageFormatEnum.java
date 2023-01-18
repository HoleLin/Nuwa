package com.holelin.messageclient.enums;
/**
 * 消息格式
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/23 22:21
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/23 22:21
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public enum MessageFormatEnum {
    /**
     * 文本消息
     */
    TEXT(1),
    /**
     * 图片
     */
    IMAGE(2),
    /**
     * 音频
     */
    AUDIO(3),
    /**
     * 视频
     */
    VIDEO(4),
    ;
    public final int code;

    MessageFormatEnum(int code) {
        this.code = code;
    }
}
