package com.holelin.messageclient.client;

import com.holelin.messageclient.protocol.MessageProtocol;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/1/12 11:01
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/1/12 11:01
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public interface SendMessageService {

    /**
     * 联通性验证
     * @param messageProto 消息体
     * @return
     */
    MessageProtocol.MessageProto pushMessage(MessageProtocol.MessageProto messageProto);

    /**
     * 登录服务器,从服务器获取当前客户端的唯一标识
     *
     * @return 客户端唯一标识
     */
    MessageProtocol.MessageProto login();

    /**
     * 创建客户端组
     *
     * @param groupId 客户端组的唯一ID
     * @return true为成功, 反之失败
     */
    MessageProtocol.MessageProto createGroup(String groupId);

    /**
     * 离开客户端组
     *
     * @param groupId 客户端组的唯一ID
     * @return true为成功, 反之失败
     */
    MessageProtocol.MessageProto leaveGroup(String groupId);

    /**
     * 根据clientId给指定的客户端发送消息
     *
     * @param clientId 客户端从服务器获取的唯一标识
     * @param content  消息内容
     * @return
     */
    MessageProtocol.MessageProto sendSingleMessage(String clientId, String content);

    /**
     * 根据groupId给指定客户端组中的客户端发送消息
     *
     * @param groupId
     * @param content
     * @return
     */
    MessageProtocol.MessageProto sendGroupMessage(String groupId, String content);

}
