package com.holelin.messageclient.enums;

/**
 * 消息发送模式
 *
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/20 22:17
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/20 22:17
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public enum ChatTypeEnum {
    /**
     * 系统模式 如PING/PONG
     */
    SYSTEM(0),
    /**
     * 单聊模式
     */
    SINGLE(1),
    /**
     * 群聊模式
     */
    GROUP(2),
    ;


    public final int code;

    ChatTypeEnum(int code) {
        this.code = code;
    }
}
