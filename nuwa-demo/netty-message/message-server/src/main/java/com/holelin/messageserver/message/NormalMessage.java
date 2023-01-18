package com.holelin.messageserver.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用消息类
 *
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/20 22:06
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/20 22:06
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
@Builder
public class NormalMessage implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 消息的唯一Id可以用 UUID 表示
     */
    private String messageId;

    /**
     * 消息发送方
     */
    private String fromUid;

    /**
     * 消息发送方
     */
    private String sendUid;

    /**
     * 发送模式
     */
    private Integer chatType;

    /**
     * 消息类型 比如登录消息、聊天消息、ack 消息、ping、pong 消息
     */
    private Integer messageType;
    /**
     * 消息格式 如文本 图片等
     */
    private Integer messageFormat;

    /**
     * 消息体
     */
    private String content;

    /**
     * 发送消息的时间戳
     */
    private Long timestamp;

}
