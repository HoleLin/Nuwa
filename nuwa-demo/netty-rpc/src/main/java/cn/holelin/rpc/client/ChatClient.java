package cn.holelin.rpc.client;

import cn.holelin.rpc.server.service.RpcService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        final RpcService service = RpcClientManager.getProxyService(RpcService.class);
        final String testRpc = service.echo("test rpc");
        final RpcService service2 = RpcClientManager.getProxyService(RpcService.class);
        final String testRpc2 = service.echo("test rpc2");

        System.out.println(testRpc);
    }
}
