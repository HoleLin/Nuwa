package com.holelin.messageclient.test;

import com.holelin.messageclient.client.WebSocketClient;
import com.holelin.messageclient.utils.SendMessageUtil;
import com.holelin.messageclient.utils.SnowflakeUtil;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

class WebSocketClientTest {

    String uri = "ws://localhost:10089/ws";

    @Test
    void ping() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        final SendMessageUtil sendMessageUtil = new SendMessageUtil(client);
        final Boolean ping = sendMessageUtil.ping();
        System.out.println(ping);
    }
    @Test
    void login() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        final SendMessageUtil sendMessageUtil = new SendMessageUtil(client);
        System.out.println(sendMessageUtil.login());
    }

    @Test
    void createGroup() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        final SendMessageUtil sendMessageUtil = new SendMessageUtil(client);
        final String clientId = sendMessageUtil.login();
        System.out.println(sendMessageUtil.createGroup(clientId, String.valueOf(SnowflakeUtil.genId())));
    }


    @Test
    void leaveGroup() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        final SendMessageUtil sendMessageUtil = new SendMessageUtil(client);
        final String clientId = sendMessageUtil.login();
        System.out.println(sendMessageUtil.leaveGroup(clientId, String.valueOf(SnowflakeUtil.genId())));
    }


    @Test
    void sendSingleMessage() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        final SendMessageUtil sendMessageUtil = new SendMessageUtil(client);
        final String clientId = sendMessageUtil.login();
        System.out.println(sendMessageUtil.sendSingleMessage(clientId, "1063424804638380032","测试消息"));
    }


    @Test
    void sendGroupMessage() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        final SendMessageUtil sendMessageUtil = new SendMessageUtil(client);
        final String clientId = sendMessageUtil.login();
        System.out.println(sendMessageUtil.sendGroupMessage(clientId, "TEXT","测试消息"));
    }


    public static void main(String[] args) throws URISyntaxException {
        String uri = "ws://localhost:10089/ws";
        final WebSocketClient client = new WebSocketClient(uri);

        final SendMessageUtil sendMessageUtil = new SendMessageUtil(client);
        System.out.println(sendMessageUtil.login());

    }



}