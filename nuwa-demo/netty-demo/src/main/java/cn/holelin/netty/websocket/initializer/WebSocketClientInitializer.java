package cn.holelin.netty.websocket.initializer;

import cn.holelin.netty.websocket.handler.WebSocketClientHandler;
import cn.holelin.netty.websocket.protocol.MessageProtocol;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;


/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/30 15:01
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/30 15:01
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class WebSocketClientInitializer extends ChannelInitializer<SocketChannel> {

    public static final String CLIENT_HANDLER_NAME = "client-handler";

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http-codec", new HttpClientCodec());
        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("protobuf-decoder", new ProtobufDecoder(MessageProtocol.MessageProto.getDefaultInstance()));
        pipeline.addLast(CLIENT_HANDLER_NAME, new WebSocketClientHandler());
        pipeline.addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
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
