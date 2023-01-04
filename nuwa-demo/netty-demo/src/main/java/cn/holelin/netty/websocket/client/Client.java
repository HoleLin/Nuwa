package cn.holelin.netty.websocket.client;


import cn.holelin.netty.websocket.message.NormalMessage;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/1/3 16:05
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/1/3 16:05
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */

public interface Client {

    /**
     * 推送消息
     *
     * @param message 消息体
     */
    void pushMessage(NormalMessage message);


    /**
     * 关闭客户端
     */
    void close();
}
