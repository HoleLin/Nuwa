package cn.holelin.netty.websocket.handler;


import cn.holelin.netty.websocket.enums.ChatTypeEnum;
import cn.holelin.netty.websocket.enums.MessageTypeEnum;
import cn.holelin.netty.websocket.protocol.MessageProtocol;
import cn.holelin.netty.websocket.utils.ServerChannelCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
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
public class SocketServerHandler extends SimpleChannelInboundHandler<MessageProtocol.MessageProto> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SocketServerHandler.class);

    @Autowired
    private ServerChannelCache channelCache;
    public static final String SYSTEM_USE_ID = "SYSTEM";


    /**
     * 通道就绪事件
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        final ChannelId id = ctx.channel().id();
        LOGGER.debug("与IP为:{}建立连接,chanelId为:{}", channel.remoteAddress(), id);
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        channelCache.loginOut(channel);
        LOGGER.info("与IP为:{}断开连接", channel.remoteAddress());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol.MessageProto message) {
        final Channel channel = ctx.channel();
        final int messageType = message.getMessageType();
        final int chatType = message.getChatType();
        // 处理系统基本功能
        if (MessageTypeEnum.PING.code == messageType
                && ChatTypeEnum.SINGLE.code == chatType) {
            handlePing(message, channel);
        }
        // 处理登录消息
        if (MessageTypeEnum.LOGIN.code == messageType
                && ChatTypeEnum.SINGLE.code == chatType) {
            handleLogin(message, channel);
        }
        // 处理创建聊天组
        if (MessageTypeEnum.CREATE_GROUP.code == messageType
                && ChatTypeEnum.SINGLE.code == chatType) {
            handleCreateGroup(message, channel);
        }

        // 处理加入聊天组
        if (MessageTypeEnum.JOIN_GROUP.code == messageType
                && ChatTypeEnum.SINGLE.code == chatType) {
            final String groupId = message.getSendUid();
            channelCache.joinGroup(groupId, channel);
        }
        // 处理离开聊天组
        if (MessageTypeEnum.LEAVE_GROUP.code == messageType
                && ChatTypeEnum.SINGLE.code == chatType) {
            final String groupId = message.getSendUid();
            channelCache.leaveGroup(groupId, channel);
        }

        // 处理聊天
        // 处理单聊

        if (MessageTypeEnum.SEND_MESSAGE.code == messageType
                && ChatTypeEnum.SINGLE.code == chatType) {
            handleSingleChat(message);
        }

        if (MessageTypeEnum.SEND_MESSAGE.code == messageType
                && ChatTypeEnum.GROUP.code == chatType) {
            handleGroupChat(message);
        }

    }

    private void handleGroupChat(MessageProtocol.MessageProto message) {
        final String groupId = message.getSendUid();
        final ChannelGroup channelGroup = channelCache.getChannelGroup(groupId);
        if (Objects.nonNull(channelGroup)) {
            channelGroup.writeAndFlush(message);
        } else {
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
        channelCache.createGroup(groupId, channel);
    }

    /**
     * 处理单聊消息
     *
     * @param message
     */
    private void handleSingleChat(MessageProtocol.MessageProto message) {
        final String sendUid = message.getSendUid();
        final Channel channel = channelCache.getChannelByUserId(sendUid);
        if (Objects.nonNull(channel)) {
            channel.writeAndFlush(message);
        } else {
            LOGGER.warn("用户:{},不在线", sendUid);
        }
    }

    /**
     * 处理登录消息
     */
    private void handleLogin(MessageProtocol.MessageProto message, Channel channel) {
        final String fromUid = message.getFromUid();
        channelCache.login(fromUid, channel);
        LOGGER.info("用户:{}上线了...", fromUid);

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
