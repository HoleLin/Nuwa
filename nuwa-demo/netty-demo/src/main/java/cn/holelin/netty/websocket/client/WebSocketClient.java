package cn.holelin.netty.websocket.client;

import cn.holelin.netty.websocket.enums.ChatTypeEnum;
import cn.holelin.netty.websocket.enums.MessageFormatEnum;
import cn.holelin.netty.websocket.enums.MessageTypeEnum;
import cn.holelin.netty.websocket.handler.WebSocketClientHandler;
import cn.holelin.netty.websocket.initializer.WebSocketClientInitializer;
import cn.holelin.netty.websocket.message.NormalMessage;
import cn.holelin.netty.websocket.protocol.MessageProtocol;
import cn.hutool.core.lang.UUID;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/1/3 16:09
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/1/3 16:09
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class WebSocketClient implements Client {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketClient.class);


    /**
     * 服务端访问地址
     * 例: ws://localhost:8080/websocketPath
     */
    private URI uri;

    private Channel channel;

    public WebSocketClient(String uri) throws URISyntaxException {
        this.uri = new URI(uri);

    }

    /**
     * 根据构造函数传入的URI连接服务器,启动客户端
     */
    public void start() {
        final NioEventLoopGroup group = new NioEventLoopGroup(new DefaultThreadFactory("WebSocketClient"));
        final Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new WebSocketClientInitializer());
            final String host = uri.getHost();
            final int port = uri.getPort();
            final ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channel = channelFuture.channel();
            channelFuture.addListener(listener -> {
                if (listener.isSuccess()) {
                    LOGGER.debug("客户端连接host:{},port:{}成功", host, port);
                } else {
                    LOGGER.error("客户端连接host:{},port:{}失败", host, port);
                }
            });
            // 通过它构造握手响应消息返回给客户端，
            // 同时将WebSocket相关的编码和解码类动态添加到ChannelPipeline中，用于WebSocket消息的编解码，
            // 添加WebSocketEncoder和WebSocketDecoder之后，服务端就可以自动对WebSocket消息进行编解码了
            WebSocketClientHandshaker handShaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
            final WebSocketClientHandler clientHandler = (WebSocketClientHandler) channel.pipeline().get(WebSocketClientInitializer.CLIENT_HANDLER_NAME);
            clientHandler.setHandShaker(handShaker);
            handShaker.handshake(channel);
            //阻塞等待是否握手成功
            final ChannelFuture handShakerChannelFuture = clientHandler.handshakeFuture().sync();
            handShakerChannelFuture.addListener(listener -> {
                if (listener.isSuccess()) {
                    LOGGER.debug("WebSocket 握手成功");
                } else {
                    LOGGER.error("WebSocket 握手失败");
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送登录消息
     */
    public void login(String uid) {
        final NormalMessage message = NormalMessage.builder()
                .content("LOGIN")
                .messageType(MessageTypeEnum.LOGIN.code)
                .chatType(ChatTypeEnum.SYSTEM.code)
                .messageId(UUID.fastUUID().toString())
                .sendUid("")
                .fromUid(uid)
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        pushMessage(message);
    }

    /**
     * 创建聊天组
     */
    public void createGroup(String groupId) {
        final NormalMessage message = NormalMessage.builder()
                .content("CREATE_GROUP")
                .messageType(MessageTypeEnum.CREATE_GROUP.code)
                .chatType(ChatTypeEnum.SYSTEM.code)
                .messageId(UUID.fastUUID().toString())
                .sendUid(groupId)
                .fromUid(UUID.fastUUID().toString())
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        pushMessage(message);
    }

    /**
     * 加入已存在的聊天组
     */
    public void joinGroup(String groupId) {
        final NormalMessage message = NormalMessage.builder()
                .content("JOIN_GROUP")
                .messageType(MessageTypeEnum.JOIN_GROUP.code)
                .chatType(ChatTypeEnum.SYSTEM.code)
                .messageId(UUID.fastUUID().toString())
                .sendUid(groupId)
                .fromUid(UUID.fastUUID().toString())
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        pushMessage(message);
    }

    /**
     * 离开加入的聊天组
     */
    public void leaveGroup(String groupId) {
        final NormalMessage message = NormalMessage.builder()
                .content("LEAVE_GROUP")
                .messageType(MessageTypeEnum.LEAVE_GROUP.code)
                .chatType(ChatTypeEnum.SYSTEM.code)
                .messageId(UUID.fastUUID().toString())
                .sendUid(groupId)
                .fromUid(UUID.fastUUID().toString())
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        pushMessage(message);

    }

    /**
     * 发送单聊消息
     *
     * @param uid     消息接收人的ID
     * @param content 消息内容
     */
    public void sendSingleMessage(String uid, String content) {
        final NormalMessage message = NormalMessage.builder()
                .content(content)
                .messageType(MessageTypeEnum.SEND_MESSAGE.code)
                .chatType(ChatTypeEnum.SINGLE.code)
                .messageId(UUID.fastUUID().toString())
                .sendUid(uid)
                .fromUid(UUID.fastUUID().toString())
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        pushMessage(message);
    }

    /**
     * 发送群组消息
     *
     * @param groupId 消息接收的群组
     * @param content 消息内容
     */
    public void sendGroupMessage(String groupId, String content) {
        final NormalMessage message = NormalMessage.builder()
                .content(content)
                .messageType(MessageTypeEnum.SEND_MESSAGE.code)
                .chatType(ChatTypeEnum.GROUP.code)
                .messageId(UUID.fastUUID().toString())
                .sendUid(groupId)
                .fromUid(UUID.fastUUID().toString())
                .messageFormat(MessageFormatEnum.TEXT.code)
                .build();
        pushMessage(message);
    }

    @Override
    public void pushMessage(NormalMessage message) {
        MessageProtocol.MessageProto messageProto = MessageProtocol.MessageProto.newBuilder()
                .setMessageId(message.getMessageId())
                .setFromUid(message.getFromUid())
                .setSendUid(message.getSendUid())
                .setChatType(message.getChatType())
                .setMessageType(message.getMessageType())
                .setMessageFormat(message.getMessageFormat())
                .setContent(message.getContent())
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli())
                .build();
        ChannelFuture future = channel.writeAndFlush(messageProto);
        future.addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        LOGGER.debug("发送消息成功,消息体:{}", messageProto);
                    }
                }

        );
    }

    @Override
    public void close() {
        channel.close();
    }
}
