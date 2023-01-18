package com.holelin.messageclient.enums;

/**
 * 消息类型枚举
 *
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/20 22:15
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/20 22:15
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public enum MessageTypeEnum {
    /**
     * 系统检测
     */
    PING(0),
    /**
     * 心跳检测
     */
    PONG(1),
    /**
     * 登录
     */
    LOGIN(2),
    /**
     * 创建聊天组
     */
    CREATE_AND_JOIN_GROUP(3),

    /**
     * 离开聊天组
     */
    LEAVE_GROUP(5),
    /**
     * 发送消息
     */
    SEND_MESSAGE(6),

    ;


    public final int code;

    MessageTypeEnum(int code) {
        this.code = code;
    }
}
