package com.holelin.messageserver.utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/22 22:45
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/22 22:45
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Component
public class ServerChannelCache {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerChannelCache.class);

    /**
     * 存储用户id和用户的channelId绑定
     */
    public ConcurrentHashMap<String, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Channel, String> CHANNEL_USER_MAP = new ConcurrentHashMap<>();
    /**
     * 用于存储群聊房间号和群聊成员的channel信息
     */
    public ConcurrentHashMap<String, ChannelGroup> GROUP_MAP = new ConcurrentHashMap<>();


    /**
     * 登录服务器
     *
     * @param id
     * @param channel
     */
    public void login(String id, Channel channel) {
        USER_CHANNEL_MAP.put(id, channel);
        CHANNEL_USER_MAP.put(channel, id);
    }

    /**
     * 登出服务器
     *
     * @param channel
     */
    public void loginOut(Channel channel) {
        final String id = CHANNEL_USER_MAP.get(channel);
        LOGGER.info("客户端ID为:{}下线..", id);
        if (Objects.nonNull(id)) {
            USER_CHANNEL_MAP.remove(id);
        }
        CHANNEL_USER_MAP.remove(channel);
    }

    public String getUserIdByChannel(Channel channel) {
        return CHANNEL_USER_MAP.get(channel);
    }

    public Channel getChannelByClientId(String clientId) {
        return USER_CHANNEL_MAP.get(clientId);
    }

    /**
     * 根据传入groupId来创建聊天组,若groupId已存在则直接将当前Channel加入聊天组
     *
     * @param groupId 聊天组唯一标识
     * @param channel 需要加入聊天组的通道
     */
    public void createAndJoinGroup(String groupId, Channel channel) {
        if (GROUP_MAP.containsKey(groupId)) {
            final ChannelGroup group = GROUP_MAP.get(groupId);
            group.add(channel);
        } else {
            final DefaultChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            group.add(channel);
            GROUP_MAP.put(groupId, group);
        }
    }


    public void leaveGroup(String groupId, Channel channel) {
        if (GROUP_MAP.containsKey(groupId)) {
            final ChannelGroup channels = GROUP_MAP.get(groupId);
            channels.remove(channel);
        }
    }

    public Boolean existsGroupId(String groupId){
        return GROUP_MAP.containsKey(groupId);
    }


    public ChannelGroup getChannelGroup(String groupId) {
        return GROUP_MAP.get(groupId);
    }

}
