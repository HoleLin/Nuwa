package com.holelin.messageclient.handler;

import com.holelin.messageclient.protocol.MessageProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @Description: WebSocket客户端消息处理类
 * @Author: HoleLin
 * @CreateDate: 2023/1/5 13:40
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/1/5 13:40
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
public class WebSocketMessageClientHandler extends SimpleChannelInboundHandler<MessageProtocol.MessageProto> implements Callable {
    /**
     * 上下文
     */
    private ChannelHandlerContext context;

    /**
     * 发送的消息
     */
    private MessageProtocol.MessageProto message;
    /**
     * 服务器返回值
     */
    private MessageProtocol.MessageProto result;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, MessageProtocol.MessageProto msg) throws Exception {
        log.info("收到服务器的消息:{}", msg.getContent());
        this.result = msg;
        notify();
    }


    @Override
    public synchronized Object call() throws Exception {

        if (Objects.nonNull(message)) {
            final Channel channel = context.pipeline().channel();
            channel.writeAndFlush(message);
        }
        // 等待服务器发送的消息
        wait();
        return result;
    }

    /**
     * 设置发送的消息
     *
     */
    public void setMessage(MessageProtocol.MessageProto message) {
        this.message = message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
