package cn.holelin.rpc.handler;

import cn.holelin.rpc.message.RpcRequestMessage;
import cn.holelin.rpc.message.RpcResponseMessage;
import cn.holelin.rpc.server.service.RpcService;
import cn.holelin.rpc.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/4/13 09:40
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/4/13 09:40
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(msg.getSequenceId());
        try {
            // 获取真正的实现对象
            final RpcService service =
                    (RpcService) ServicesFactory.getService(Class.forName(msg.getInterfaceFullName()));
            // 获取要调用的方法
            final Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            // 调用方法
            final Object object = method.invoke(service, msg.getParameterValue());
            response.setReturnValue(object);

        } catch (Exception e) {
            e.printStackTrace();
            // 调用异常
            response.setExceptionValue(new Exception("调用出错:" + e.getCause().getMessage()));
        }
        ctx.writeAndFlush(response);
    }
}
