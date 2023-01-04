package cn.holelin.netty.websocket.server;

import cn.holelin.netty.websocket.initializer.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/22 22:14
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/22 22:14
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Component
public class WebSocketServer implements ApplicationRunner {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);

    @Autowired
    private WebSocketServerInitializer initializer;
    /**
     * 主线程组的线程个数
     */
    public static final Integer MAIN_THREAD_NUMBER = 1;
    /**
     * 服务器监听的端口
     */
    @Value("${message.websocket.port:10089}")
    private Integer port;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        final NioEventLoopGroup boosGroup = new NioEventLoopGroup(MAIN_THREAD_NUMBER);
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler("DEBUG"))
                    .childHandler(initializer)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            final ChannelFuture channelFuture = serverBootstrap.bind(port);
            channelFuture.addListener(listener -> {
                if (listener.isSuccess()) {
                    LOGGER.info("WebSocket绑定端口:{}成功", port);
                } else {
                    LOGGER.warn("WebSocket绑定端口:{}失败", port);
                }
            });
            final Channel channel = channelFuture.sync().channel();
            channel.closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
