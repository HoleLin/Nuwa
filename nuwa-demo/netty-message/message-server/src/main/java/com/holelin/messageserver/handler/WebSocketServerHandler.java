package com.holelin.messageserver.handler;

import com.holelin.messageserver.consts.StringConstants;
import com.holelin.messageserver.enums.ChatTypeEnum;
import com.holelin.messageserver.enums.MessageTypeEnum;
import com.holelin.messageserver.protocol.MessageProtocol;
import com.holelin.messageserver.utils.ServerChannelCache;
import com.holelin.messageserver.utils.SnowflakeUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/16 10:41
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/16 10:41
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Component
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<MessageProtocol.MessageProto> {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketServerHandler.class);

    @Autowired
    private ServerChannelCache channelCache;
    public static final String SYSTEM_USE_ID = "SYSTEM";


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {

            //将  evt 向下转型 IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()) {
                case ALL_IDLE:
                    eventType = "读写空闲";
                    final Channel channel = ctx.channel();
                    channelCache.loginOut(channel);
                    channel.close();
                    break;
            }
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol.MessageProto message) {
        final Channel channel = ctx.channel();
        final int messageType = message.getMessageType();
        final int chatType = message.getChatType();
        // 处理系统基本功能
        if (MessageTypeEnum.PING.code == messageType
                && ChatTypeEnum.SYSTEM.code == chatType) {
            handlePing(message, channel);
        }
        // 处理登录消息
        if (MessageTypeEnum.LOGIN.code == messageType
                && ChatTypeEnum.SYSTEM.code == chatType) {
            handleLogin(channel);
        }
        // 处理创建聊天组
        if (MessageTypeEnum.CREATE_AND_JOIN_GROUP.code == messageType
                && ChatTypeEnum.SYSTEM.code == chatType) {
            handleCreateGroup(message, channel);
        }

        // 处理离开聊天组
        if (MessageTypeEnum.LEAVE_GROUP.code == messageType
                && ChatTypeEnum.SYSTEM.code == chatType) {
            handleLevelGroup(message, channel);
        }

        // 处理聊天
        // 处理单聊
        if (MessageTypeEnum.SEND_MESSAGE.code == messageType
                && ChatTypeEnum.SINGLE.code == chatType) {
            handleSingleChat(message, channel);
        }

        if (MessageTypeEnum.SEND_MESSAGE.code == messageType
                && ChatTypeEnum.GROUP.code == chatType) {
            handleGroupChat(message, channel);
        }

    }

    private void handleLevelGroup(MessageProtocol.MessageProto message, Channel channel) {
        final String fromUid = message.getFromUid();
        final String groupId = message.getSendUid();
        final Channel fromChannel = channelCache.getChannelByClientId(fromUid);
        final MessageProtocol.MessageProto.Builder builder = MessageProtocol.MessageProto.newBuilder()
                .setMessageType(MessageTypeEnum.LEAVE_GROUP_RESPONSE.code)
                .setFromUid(SYSTEM_USE_ID)
                .setSendUid(fromUid);
        if (Objects.nonNull(fromChannel)) {
            if (channelCache.existsGroupId(groupId)) {
                channelCache.leaveGroup(groupId, channel);
                fromChannel.writeAndFlush(builder.setContent(StringConstants.TRUE).build());
                LOGGER.info("客户端ID:{}离开客户端组:{}", fromUid, groupId);
            } else {
                fromChannel.writeAndFlush(builder.setContent(StringConstants.FALSE).build());
                LOGGER.warn("客户端ID:{}离开客户端组:{}失败,租户端组不存在", fromUid, groupId);
            }
        }
    }

    private void handleGroupChat(MessageProtocol.MessageProto message, Channel currentChannel) {
        final String groupId = message.getSendUid();
        final String fromUid = message.getFromUid();
        final ChannelGroup channelGroup = channelCache.getChannelGroup(groupId);
        final MessageProtocol.MessageProto.Builder builder = MessageProtocol.MessageProto.newBuilder()
                .setMessageType(MessageTypeEnum.SEND_MESSAGE_RESPONSE.code)
                .setChatType(ChatTypeEnum.GROUP.code)
                .setFromUid(SYSTEM_USE_ID)
                .setSendUid(fromUid);
        if (Objects.nonNull(channelGroup) && Objects.nonNull(channelGroup.find(currentChannel.id()))) {
            channelGroup.writeAndFlush(message);
            LOGGER.info("发送客户端组消息成功,消息为:{}", message);
            // 返回值
            currentChannel.writeAndFlush(builder.setContent(StringConstants.TRUE).build());
        } else {
            currentChannel.writeAndFlush(builder.setContent(StringConstants.FALSE).build());
            LOGGER.warn("{}组不存在", groupId);
        }
    }

    /**
     * 处理创建聊天组
     *
     * @param message
     * @param channel
     */
    private void handleCreateGroup(MessageProtocol.MessageProto message, Channel channel) {
        final String groupId = message.getSendUid();
        final String fromUid = message.getFromUid();
        final Channel fromChannel = channelCache.getChannelByClientId(fromUid);
        final MessageProtocol.MessageProto.Builder builder = MessageProtocol.MessageProto.newBuilder()
                .setMessageType(MessageTypeEnum.CREATE_AND_JOIN_GROUP_RESPONSE.code)
                .setFromUid(SYSTEM_USE_ID)
                .setSendUid(fromUid);
        channelCache.createAndJoinGroup(groupId, channel);
        if (Objects.nonNull(fromChannel)) {
            fromChannel.writeAndFlush(builder.setContent(StringConstants.TRUE).build());
            LOGGER.info("用户:{}加入用户组:{}成功", fromUid, groupId);
        }
    }

    /**
     * 处理单聊消息
     *
     * @param message
     */
    private void handleSingleChat(MessageProtocol.MessageProto message, Channel currentChannel) {
        final String sendUid = message.getSendUid();
        final String fromUid = message.getFromUid();
        final Channel channel = channelCache.getChannelByClientId(sendUid);
        final MessageProtocol.MessageProto.Builder builder = MessageProtocol.MessageProto.newBuilder()
                .setMessageType(MessageTypeEnum.SEND_MESSAGE_RESPONSE.code)
                .setChatType(ChatTypeEnum.SINGLE.code)
                .setFromUid(SYSTEM_USE_ID)
                .setSendUid(fromUid);
        if (Objects.nonNull(channel)) {
            channel.writeAndFlush(message);
            currentChannel.writeAndFlush(builder.setContent(StringConstants.TRUE).build());
            LOGGER.info("发送客户端消息成功,消息为:{}", message);
        } else {
            currentChannel.writeAndFlush(builder.setContent(StringConstants.FALSE).build());
            LOGGER.warn("用户:{},不在线", sendUid);
        }
    }

    /**
     * 处理登录消息
     */
    private void handleLogin(Channel channel) {
        final String clientId = String.valueOf(SnowflakeUtil.genId());
        channelCache.login(clientId, channel);
        final MessageProtocol.MessageProto responseMessage = MessageProtocol.MessageProto.newBuilder()
                .setMessageType(MessageTypeEnum.LOGIN_RESPONSE.code)
                .setFromUid(clientId)
                .setSendUid(SYSTEM_USE_ID)
                .setContent(clientId)
                .build();
        channel.writeAndFlush(responseMessage);
        LOGGER.info("用户:{}上线了...", clientId);
    }

    /**
     * 处理PING消息
     *
     * @param message
     * @param channel
     */
    private void handlePing(MessageProtocol.MessageProto message, Channel channel) {
        final String fromUid = message.getFromUid();
        // 构建PONG消息
        final MessageProtocol.MessageProto pongMessage = MessageProtocol.MessageProto.newBuilder()
                .setMessageType(MessageTypeEnum.PONG.code)
                .setFromUid(SYSTEM_USE_ID)
                .setSendUid(fromUid)
                .setContent(MessageTypeEnum.PONG.toString())
                .build();

        channel.writeAndFlush(pongMessage);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
