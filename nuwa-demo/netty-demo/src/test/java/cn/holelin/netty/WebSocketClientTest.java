package cn.holelin.netty;


import cn.holelin.netty.websocket.client.WebSocketClient;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

class WebSocketClientTest {

    String uri = "ws://localhost:10089/ws";
    String TEST_GROUP_ID = "test";
    String TEST_CLIENT_ID = "test_client";

    @Test
    public void test() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        client.start();
    }

    @Test
    public void testLogin() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        client.start();
        client.login(TEST_CLIENT_ID);
    }

    @Test
    public void testCreateGroup() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        client.start();
        client.createGroup(TEST_GROUP_ID);
    }

    @Test
    public void testJoinGroup() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        client.start();
        client.joinGroup(TEST_GROUP_ID);
    }

    @Test
    public void testLeaveGroup() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        client.start();
        client.leaveGroup(TEST_GROUP_ID);
    }

    @Test
    public void testSendSingleMessage() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        client.start();
        client.sendSingleMessage("test2","你好");
    }

    @Test
    public void testSendGroupMessage() throws URISyntaxException {
        final WebSocketClient client = new WebSocketClient(uri);
        client.start();
        client.sendGroupMessage(TEST_GROUP_ID,"你们好");
    }
}