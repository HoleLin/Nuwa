package com.holelin.messageserver.initializer;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.holelin.messageserver.handler.WebSocketServerHandler;
import com.holelin.messageserver.protocol.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/16 10:26
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/16 10:26
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Component
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private WebSocketServerHandler webSocketServerHandler;

    @Value("${message.websocket.work-path:/ws}")
    private String workPath;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        final ChannelPipeline pipeline = ch.pipeline();
         /*
         说明
         1. IdleStateHandler 是netty 提供的处理空闲状态的处理器
         2. long readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接
         3. long writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
         4. long allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接
         5. 文档说明
         triggers an {@link IdleStateEvent} when a {@link Channel} has not performed
        read, write, or both operation for a while.
         6. 当 IdleStateEvent 触发后 , 就会传递给管道 的下一个handler去处理，通过调用(触发)
        下一个handler 的 userEventTriggered , 在该方法中去处理 IdleStateEvent(读空闲，写空闲，读写空闲)
         7.handlerRemoved有时候是无法感知连接断掉，所以还是需要心跳包的检测来判断连接是否还有效
         */
        pipeline.addLast(new IdleStateHandler(6, 6, 6, TimeUnit.HOURS));
        pipeline.addLast("http-server-codec", new HttpServerCodec());
        pipeline.addLast("chunk-write", new ChunkedWriteHandler());
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(64 * 1024));
        // WebSocket数据压缩
        pipeline.addLast(new WebSocketServerCompressionHandler());
        // 协议包长度限制
        pipeline.addLast(new WebSocketServerProtocolHandler(workPath, null, true));
        // 协议包解码
        pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                ByteBuf buf = frame.content();
                buf.retain();
                objs.add(buf);
            }
        });
        pipeline.addLast("protobuf-decoder", new ProtobufDecoder(MessageProtocol.MessageProto.getDefaultInstance()));
        pipeline.addLast(webSocketServerHandler);

        // 协议包编码
        pipeline.addLast("custom-encoder", new MessageToMessageEncoder<MessageLiteOrBuilder>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                ByteBuf result = null;
                if (msg instanceof MessageLite) {
                    result = wrappedBuffer(((MessageLite) msg).toByteArray());
                }
                if (msg instanceof MessageLite.Builder) {
                    result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                }
                // ==== 上面代码片段是拷贝自TCP ProtobufEncoder 源码 ====
                // 然后下面再转成websocket二进制流，因为客户端不能直接解析protobuf编码生成的
                assert result != null;
                WebSocketFrame frame = new BinaryWebSocketFrame(result);
                out.add(frame);
            }
        });
    }
}
