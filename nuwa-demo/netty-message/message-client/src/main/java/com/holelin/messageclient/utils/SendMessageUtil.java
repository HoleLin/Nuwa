package com.holelin.messageclient.utils;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.holelin.messageclient.client.SendMessageService;
import com.holelin.messageclient.client.WebSocketClient;
import com.holelin.messageclient.enums.ChatTypeEnum;
import com.holelin.messageclient.enums.MessageFormatEnum;
import com.holelin.messageclient.enums.MessageTypeEnum;
import com.holelin.messageclient.message.NormalMessage;
import com.holelin.messageclient.protocol.MessageProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.holelin.messageclient.consts.StringConstants.EMPTY;
import static com.holelin.messageclient.consts.StringConstants.OPERATION_CREATE_GROUP;
import static com.holelin.messageclient.consts.StringConstants.OPERATION_LEAVE_GROUP;
import static com.holelin.messageclient.consts.StringConstants.OPERATION_LOGIN;
import static com.holelin.messageclient.consts.StringConstants.OPERATION_PING;
import static com.holelin.messageclient.consts.StringConstants.OPERATION_PONG;
import static com.holelin.messageclient.consts.StringConstants.TRUE;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/1/13 11:03
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/1/13 11:03
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class SendMessageUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(SendMessageUtil.class);


    private final SendMessageService sendMessageService;

    public SendMessageUtil(WebSocketClient client) {
        this.sendMessageService = (SendMessageService) client.getBean(SendMessageService.class);
    }

    /**
     * 连通性验证
     *
     * @return true为成功, 反之失败
     */
    public Boolean ping() {
        final NormalMessage message = NormalMessage.builder()
                .content(OPERATION_PING)
                .messageType(MessageTypeEnum.PING.code)
                .chatType(ChatTypeEnum.SYSTEM.code)
                .sendUid(EMPTY)
                .fromUid(EMPTY)
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        try {
            final MessageProtocol.MessageProto response = sendMessageService.pushMessage(buildMessageProto(message));
            return OPERATION_PONG.equals(response.getContent());
        }catch (Exception e){
            LOGGER.error("连接失败");
            return false;
        }
    }

    /**
     * 登录服务器,从服务器获取当前客户端的唯一标识
     *
     * @return 客户端唯一标识
     */
    public String login() {
        if (!ping()){
            return EMPTY;
        }
        final NormalMessage message = NormalMessage.builder()
                .content(OPERATION_LOGIN)
                .messageType(MessageTypeEnum.LOGIN.code)
                .chatType(ChatTypeEnum.SYSTEM.code)
                .sendUid(EMPTY)
                .fromUid(EMPTY)
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        final MessageProtocol.MessageProto response = sendMessageService.pushMessage(buildMessageProto(message));
        final String content = response.getContent();
        if (StrUtil.isNotEmpty(content)) {
            return content;
        }
        return EMPTY;
    }

    /**
     * 创建客户端组
     *
     * @param fromClientId 发送请求的客户端ID
     * @param groupId      客户端组的唯一ID
     * @return true为成功, 反之失败
     */
    public Boolean createGroup(String fromClientId, String groupId) {
        if (!ping()){
            return false;
        }
        final NormalMessage message = NormalMessage.builder()
                .content(OPERATION_CREATE_GROUP)
                .messageType(MessageTypeEnum.CREATE_AND_JOIN_GROUP.code)
                .chatType(ChatTypeEnum.SYSTEM.code)
                .sendUid(groupId)
                .fromUid(fromClientId)
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        final MessageProtocol.MessageProto response = sendMessageService.pushMessage(buildMessageProto(message));
        final String content = response.getContent();
        return TRUE.equals(content);
    }

    /**
     * 离开客户端组
     *
     * @param fromClientId 发送请求的客户端ID
     * @param groupId      客户端组的唯一ID
     * @return true为成功, 反之失败
     */
    public Boolean leaveGroup(String fromClientId, String groupId) {
        if (!ping()){
            return false;
        }
        final NormalMessage message = NormalMessage.builder()
                .content(OPERATION_LEAVE_GROUP)
                .messageType(MessageTypeEnum.LEAVE_GROUP.code)
                .chatType(ChatTypeEnum.SYSTEM.code)
                .sendUid(groupId)
                .fromUid(fromClientId)
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        final MessageProtocol.MessageProto response = sendMessageService.pushMessage(buildMessageProto(message));
        final String content = response.getContent();
        return TRUE.equals(content);
    }

    /**
     * 根据clientId给指定的客户端发送消息
     *
     * @param fromClientId 消息发送方的客户端唯一标识
     * @param clientId     消息接收方的客户端唯一标识
     * @param content      消息内容
     * @return true为成功, 反之失败
     */
    public Boolean sendSingleMessage(String fromClientId, String clientId, String content) {
        if (!ping()){
            return false;
        }
        final NormalMessage message = NormalMessage.builder()
                .content(content)
                .messageType(MessageTypeEnum.SEND_MESSAGE.code)
                .chatType(ChatTypeEnum.SINGLE.code)
                .sendUid(clientId)
                .fromUid(fromClientId)
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        final MessageProtocol.MessageProto response = sendMessageService.pushMessage(buildMessageProto(message));
        final String responseContent = response.getContent();
        return TRUE.equals(responseContent);
    }

    /**
     * 根据groupId给指定客户端组中的客户端发送消息
     *
     * @param fromClientId 消息发送方的客户端唯一标识
     * @param groupId      客户端组的唯一ID
     * @param content      消息内容
     * @return true为成功, 反之失败
     */
    public Boolean sendGroupMessage(String fromClientId, String groupId, String content) {
        if (!ping()){
            return false;
        }
        final NormalMessage message = NormalMessage.builder()
                .content(content)
                .messageType(MessageTypeEnum.SEND_MESSAGE.code)
                .chatType(ChatTypeEnum.GROUP.code)
                .sendUid(groupId)
                .fromUid(fromClientId)
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        final MessageProtocol.MessageProto response = sendMessageService.pushMessage(buildMessageProto(message));
        final String responseContent = response.getContent();
        return TRUE.equals(responseContent);
    }

    /**
     * 构建Protobuf消息体
     *
     * @param message 消息
     * @return
     */
    private static MessageProtocol.MessageProto buildMessageProto(NormalMessage message) {
        return MessageProtocol.MessageProto.newBuilder()
                .setMessageId(UUID.fastUUID().toString())
                .setFromUid(message.getFromUid())
                .setSendUid(message.getSendUid())
                .setChatType(message.getChatType())
                .setMessageType(message.getMessageType())
                .setMessageFormat(message.getMessageFormat())
                .setContent(message.getContent())
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli())
                .build();
    }
}
