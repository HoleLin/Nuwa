package cn.holelin.rpc.server.service;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/4/13 09:47
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/4/13 09:47
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class RpcServiceImpl implements RpcService {

    @Override
    public String echo(String str) {
        int i = 1 / 0;
        return "Echo: " + str;
    }
}
