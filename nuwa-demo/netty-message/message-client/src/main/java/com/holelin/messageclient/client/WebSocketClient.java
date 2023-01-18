package com.holelin.messageclient.client;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.holelin.messageclient.handler.WebSocketClientHandler;
import com.holelin.messageclient.handler.WebSocketMessageClientHandler;
import com.holelin.messageclient.protocol.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.buffer.Unpooled.wrappedBuffer;


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

    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static final String CLIENT_HANDLER_NAME = "client-handler";


    /**
     * 服务端访问地址
     * 例: ws://localhost:8080/websocketPath
     */
    private URI uri;

    private Channel channel;
    private WebSocketMessageClientHandler messageClientHandler;

    public WebSocketClient(String uri) throws URISyntaxException {
        this.uri = new URI(uri);

    }

    /**
     * 根据构造函数传入的URI连接服务器,启动客户端
     */
    public void initClient() {
        final NioEventLoopGroup group = new NioEventLoopGroup(new DefaultThreadFactory("WebSocketClient"));
        final Bootstrap bootstrap = new Bootstrap();
        messageClientHandler = new WebSocketMessageClientHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast("http-codec", new HttpClientCodec());
                        pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));
                        pipeline.addLast(WebSocketClientCompressionHandler.INSTANCE);
                        // 都属于ChannelInboundHandler，按照顺序执行
                        // 协议包解码
                        pipeline.addLast("websocket-decoder", new MessageToMessageDecoder<WebSocketFrame>() {
                            @Override
                            protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                                ByteBuf buf = frame.content();
                                objs.add(buf);
                                buf.retain();
                            }
                        });
                        pipeline.addLast("protobuf-decoder", new ProtobufDecoder(MessageProtocol.MessageProto.getDefaultInstance()));
                        pipeline.addLast("message-handler", messageClientHandler);
                        pipeline.addLast(CLIENT_HANDLER_NAME, new WebSocketClientHandler());

                        // 都属于ChannelOutboundHandler，逆序执行
                        // 编码器
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
                });
        try {
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
            final WebSocketClientHandler clientHandler = (WebSocketClientHandler) channel.pipeline().get(CLIENT_HANDLER_NAME);
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

    public Object getBean(final Class<?> serviceClass) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass},
                ((proxy, method, args) -> {
                    if (Objects.isNull(messageClientHandler)) {
                        initClient();
                    }
                    messageClientHandler.setMessage((MessageProtocol.MessageProto) args[0]);
                    return executor.submit(messageClientHandler).get();
                }));
    }


    @Override
    public void close() {
        channel.close();
    }
}
