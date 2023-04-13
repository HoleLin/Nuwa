### Message-Netty

* 基于`Netty`实现的`WebSocket`服务,并采用`Protobuf`进行数据传输

#### 项目结构

* 该项目主要分两个部分`message-server`和`message-client`
  * `message-client`: 基于`Netty`实现的`WebSocket`客户端,具有与`message-server`交互的功能.
  * `message-server`: 基于`SpringBoot`结合`Netty`实现的`WebSocket`服务端.

```sh
├── README.md
├── message-client
│   ├── pom.xml
│   └── src
│       ├── main
│       │   └── java
│       │       └── com
│       │           └── holelin
│       │               └── messageclient
│       │                   ├── client
│       │                   │   ├── Client.java
│       │                   │   ├── SendMessageService.java
│       │                   │   └── WebSocketClient.java
│       │                   ├── consts
│       │                   │   └── StringConstants.java
│       │                   ├── enums
│       │                   │   ├── ChatTypeEnum.java
│       │                   │   ├── MessageFormatEnum.java
│       │                   │   └── MessageTypeEnum.java
│       │                   ├── handler
│       │                   │   ├── WebSocketClientHandler.java
│       │                   │   └── WebSocketMessageClientHandler.java
│       │                   ├── message
│       │                   │   └── NormalMessage.java
│       │                   ├── protocol
│       │                   │   ├── MessageProtocol.java
│       │                   │   └── MessageProtocol.proto
│       │                   └── utils
│       │                       ├── SendMessageUtil.java
│       │                       └── SnowflakeUtil.java
│       └── test
│           └── java
│               └── com
│                   └── holelin
│                       └── messageclient
│                           └── test
│                               └── WebSocketClientTest.java
├── message-server
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── holelin
│       │   │           └── messageserver
│       │   │               ├── MessageServerApplication.java
│       │   │               ├── consts
│       │   │               │   └── StringConstants.java
│       │   │               ├── enums
│       │   │               │   ├── ChatTypeEnum.java
│       │   │               │   ├── MessageFormatEnum.java
│       │   │               │   └── MessageTypeEnum.java
│       │   │               ├── handler
│       │   │               │   └── WebSocketServerHandler.java
│       │   │               ├── initializer
│       │   │               │   └── WebSocketServerInitializer.java
│       │   │               ├── message
│       │   │               │   └── NormalMessage.java
│       │   │               ├── protocol
│       │   │               │   ├── MessageProtocol.java
│       │   │               │   └── MessageProtocol.proto
│       │   │               ├── server
│       │   │               │   └── WebSocketServer.java
│       │   │               └── utils
│       │   │                   ├── ServerChannelCache.java
│       │   │                   └── SnowflakeUtil.java
│       │   └── resources
│       │       ├── application.yaml
│       │       └── front
│       │           ├── MessageProtocol.proto
│       │           ├── index.html
│       │           └── protobuf.min.js
│       └── test
│           └── java
│               └── com
│                   └── holelin
│                       └── messageserver
│                           └── MessageServerApplicationTests.java
└── pom.xml
```

#### 项目启动方式

* `message-server`: 以SpringBoot项目启动方式启动

* `message-client`: 为封装的与`message-server`交互的API,其中具有连通性验证,登录服务器,创建并加入客户端组,离开客户端组,给指定客户端发送消息,给指定客户端组发送消息等功能.

  ```java
  package com.diannei.ai.messageclient.client;
  
  
  import com.diannei.ai.messageclient.utils.SendMessageUtil;
  import com.diannei.ai.messageclient.utils.SnowflakeUtil;
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
  }
  ```

#### 项目调试

* 调试可以使用两种方式:

  * Java-Client-API方式,即上方的单元测试类

  * 前端JS调试

    * 调试文件位于`message-server/src/main/resources/front`目录下

    ```html
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>WebSocket客户端</title>
    </head>
    <body>
    
    <script src="protobuf.min.js"></script>
    
    <script type="text/javascript">
        let socket;
        const SEND_MESSAGE = 6;
        const PONG = 1;
        const LOGIN_RESPONSE = 20;
        const CREATE_AND_JOIN_GROUP_RESPONSE = 30;
        const LEAVE_GROUP_RESPONSE = 50;
        const SEND_MESSAGE_RESPONSE = 60;
    
        const CHAT_SIGNLE = 1;
        const CHAT_GROUP = 2;
        let currentClientId;
    
        //如果浏览器支持WebSocket
        if (window.WebSocket) {
            //参数就是与服务器连接的地址
            socket = new WebSocket("ws://192.168.253.246:10089/ws");
            //客户端收到服务器消息的时候就会执行这个回调方法
            socket.onmessage = function (event) {
                const ta = document.getElementById("responseText");
                // 解码
                messageDecoder({
                    data: event.data,
                    success: function (responseMessage) {
                        console.log("返回值为", responseMessage)
                        if (LOGIN_RESPONSE === responseMessage.messageType) {
                            handleLoginRes(responseMessage, ta);
                        }
                        if (CREATE_AND_JOIN_GROUP_RESPONSE === responseMessage.messageType) {
                            handleCreateAndJoinGroupRes(responseMessage, ta);
                        }
                        if (LEAVE_GROUP_RESPONSE === responseMessage.messageType) {
                            handleLeaveGroupRes(responseMessage, ta);
                        }
    
                        if (SEND_MESSAGE_RESPONSE === responseMessage.messageType) {
                            handleSendMessageRes(responseMessage, ta);
                        }
                        if (SEND_MESSAGE === responseMessage.messageType && CHAT_SIGNLE === responseMessage.chatType) {
                            handleSingleRes(responseMessage, ta);
                        }
                        if (SEND_MESSAGE === responseMessage.messageType && CHAT_GROUP === responseMessage.chatType) {
                            handleGroupRes(responseMessage, ta);
                        }
                        if (PONG === responseMessage.messageType) {
                            handlePingRes(responseMessage,ta);
                        }
                    },
                    fail: function (err) {
                        console.log(err);
                    },
                    complete: function () {
                        console.log("解码全部完成")
                    }
                })
            }
            //连接建立的回调函数
            socket.onopen = function (event) {
                const ta = document.getElementById("responseText");
                ta.value = "连接开启";
            }
            //连接断掉的回调函数
            socket.onclose = function (event) {
                const ta = document.getElementById("responseText");
                ta.value = ta.value + "\n" + "连接关闭";
            }
        } else {
            alert("浏览器不支持WebSocket！");
        }
    
        /**
         *============================
         * 发送数据
         *============================
         */
    
        //发送数据
        function send(message) {
            if (!window.WebSocket) {
                return;
            }
            // socket.binaryType = "arraybuffer";
            // 判断是否开启
            if (socket.readyState !== WebSocket.OPEN) {
                alert("连接没有开启");
                return;
            }
            const data = JSON.parse(JSON.stringify(message));
            console.log(data);
            messageEncoder({
                data: data,
                success: function (buffer) {
                    console.log("编码成功");
                    socket.send(buffer);
                },
                fail: function (err) {
                    console.log(err);
                },
                complete: function () {
                    console.log("编码全部完成")
                }
            });
        }
        function ping() {
            if (!window.WebSocket) {
                return;
            }
            // socket.binaryType = "arraybuffer";
            // 判断是否开启
            if (socket.readyState !== WebSocket.OPEN) {
                alert("连接没有开启");
                return;
            }
            const data = {
                "fromUid": "",
                "sendUid": "",
                "chatType": 0,
                "messageType": 0,
                "messageFormat": 0,
                "content": ""
            };
            send(data);
    
        }
        function login(userId) {
            if (!window.WebSocket) {
                return;
            }
            // socket.binaryType = "arraybuffer";
            // 判断是否开启
            if (socket.readyState !== WebSocket.OPEN) {
                alert("连接没有开启");
                return;
            }
            const data = {
                "fromUid": "",
                "sendUid": "",
                "chatType": 0,
                "messageType": 2,
                "messageFormat": 0,
                "content": userId
            };
            send(data);
    
        }
        function loginOut() {
            if (!window.WebSocket) {
                return;
            }
            // socket.binaryType = "arraybuffer";
            // 判断是否开启
            if (socket.readyState !== WebSocket.OPEN) {
                alert("连接没有开启");
                return;
            }
            const data = {
                "fromUid": "",
                "sendUid": "",
                "chatType": 0,
                "messageType": 7,
                "messageFormat": 0,
                "content": ""
            };
            send(data);
        }
    
        function createGroup(groupId) {
            if (!window.WebSocket) {
                return;
            }
            // socket.binaryType = "arraybuffer";
            // 判断是否开启
            if (socket.readyState !== WebSocket.OPEN) {
                alert("连接没有开启");
                return;
            }
            let data = {
                "fromUid": currentClientId,
                "sendUid": groupId,
                "chatType": 0,
                "messageType": 3,
                "messageFormat": 0,
                "content": "CREATE_GROUP"
            };
            send(data);
        }
    
        function leaveGroup(groupId) {
            if (!window.WebSocket) {
                return;
            }
            // socket.binaryType = "arraybuffer";
            // 判断是否开启
            if (socket.readyState !== WebSocket.OPEN) {
                alert("连接没有开启");
                return;
            }
            const data = {
                "fromUid": currentClientId,
                "sendUid": groupId,
                "chatType": 0,
                "messageType": 5,
                "messageFormat": 0,
                "content": "LEAVE_GROUP"
            };
            send(data);
        }
    
        function sendSingleMessage(sendToClientId, content) {
            if (!window.WebSocket) {
                return;
            }
            // 判断是否开启
            if (socket.readyState !== WebSocket.OPEN) {
                alert("连接没有开启");
                return;
            }
            const data = {
                "fromUid": currentClientId,
                "sendUid": sendToClientId,
                "chatType": 1,
                "messageType": 6,
                "messageFormat": 0,
                "content": content
            };
            send(data);
        }
    
        function sendGroupMessage(groupId, content) {
            if (!window.WebSocket) {
                return;
            }
            // 判断是否开启
            if (socket.readyState !== WebSocket.OPEN) {
                alert("连接没有开启");
                return;
            }
            const data = {
                "fromUid": currentClientId,
                "sendUid": groupId,
                "chatType": 2,
                "messageType": 6,
                "messageFormat": 0,
                "content": content
            };
            send(data);
        }
    
        /**
         *============================
         * 返回值处理
         *============================
         */
        function handleGroupRes(resp, ta) {
            let fromUid = resp.fromUid
            let content = resp.content;
            ta.value = ta.value + "\n收到来自:" + fromUid + "的群组消息:" + content;
        }
    
        function handleSingleRes(resp, ta) {
            let content = resp.content;
            let fromUid = resp.fromUid
            ta.value = ta.value + "\n收到来自:" + fromUid + "的单聊消息:" + content;
        }
    
        function handleLoginRes(resp, ta) {
            let content = resp.content;
            if(content===''){
                ta.value = ta.value + "\n登录失败";
    
            }else{
                currentClientId = content;
            console.log("currentClientId:" + currentClientId)
            ta.value = ta.value + "\n当前登录的ClientId为:" + content;
            }
        }
    
        function handleCreateAndJoinGroupRes(resp, ta) {
            let content = resp.content;
            if ("TRUE" === content) {
                ta.value = ta.value + "\n创建客户端组成功";
            } else {
                ta.value = ta.value + "\n创建客户端组失败";
            }
        }
    
        function handleLeaveGroupRes(resp, ta) {
            let content = resp.content;
            if ("TRUE" === content) {
                ta.value = ta.value + "\n离开客户端组成功";
            } else {
                ta.value = ta.value + "\n离开客户端组失败";
            }
        }
    
        function handleSendMessageRes(resp, ta) {
            let content = resp.content;
            if ("TRUE" === content) {
                ta.value = ta.value + "\n发送消息成功";
            } else {
                ta.value = ta.value + "\n发送消息失败";
            }
        }
        function handlePingRes(resp, ta) {
            let content = resp.content;
            ta.value = ta.value + "\n"+content;
        }
    
        /**
         *============================
         * protobuf 编解码处理
         *============================
         */
        /**
         * 发送的消息编码成 protobuf
         */
        function messageEncoder(obj) {
            let data = obj.data;
            let success = obj.success; // 成功的回调
            let fail = obj.fail; // 失败的回调
            let complete = obj.complete; // 成功或者失败都会回调
            protobuf.load("./MessageProtocol.proto", function (err, root) {
                if (err) {
                    if (typeof fail === "function") {
                        fail(err)
                    }
                    if (typeof complete === "function") {
                        complete()
                    }
                    return;
                }
                // Obtain a message type
                let MessageProto = root.lookupType("MessageProto");
                console.log(MessageProto);
    
                // Exemplary payload
                let payload = data;
                // Verify the payload if necessary (i.e. when possibly incomplete or invalid)
                let errMsg = MessageProto.verify(payload);
                if (errMsg) {
                    if (typeof fail === "function") {
                        fail(errMsg)
                    }
                    if (typeof complete === "function") {
                        complete()
                    }
                    return;
                }
                // Create a new message
                let message = MessageProto.create(payload); // or use .fromObject if conversion is necessary
                // Encode a message to an Uint8Array (browser) or Buffer (node)
                let buffer = MessageProto.encode(message).finish();
                if (typeof success === "function") {
                    success(buffer)
                }
                if (typeof complete === "function") {
                    complete()
                }
            });
        }
    
        /**
         * 接收到服务器二进制流的消息进行解码
         */
        function messageDecoder(obj) {
            let data = obj.data;
            let success = obj.success; // 成功的回调
            let fail = obj.fail; // 失败的回调
            let complete = obj.complete; // 成功或者失败都会回调
            protobuf.load("./MessageProtocol.proto", function (err, root) {
                if (err) {
                    if (typeof fail === "function") {
                        fail(err)
                    }
                    if (typeof complete === "function") {
                        complete()
                    }
                    return;
                }
                // Obtain a message type
                let MessageProto = root.lookupType("MessageProto");
                console.log(MessageProto);
                let reader = new FileReader();
                reader.readAsArrayBuffer(data);
                reader.onload = function (e) {
                    let buf = new Uint8Array(reader.result);
                    let messageProto = MessageProto.decode(buf);
                    console.log(messageProto);
                    if (typeof success === "function") {
                        success(messageProto)
                    }
                    if (typeof complete === "function") {
                        complete()
                    }
                }
            });
        }
    </script>
    
    <h1>欢迎访问客服系统</h1>
    
    
    <form onsubmit="return false">
        <h4>连通性测试</h4>
        <input type="button" value="ping" onclick="ping()">
    
        <h4>用户登录：</h4>
        userId:<input type="text" name="userId">
        <input type="button" value="登录" onclick="login(this.form.userId.value)">
        <input type="button" value="登出" onclick="loginOut()">
    
        <br/>
        <br/>
        GroupId:<input type="text" name="groupId">
        <input type="button" value="加入客户端组" onclick="createGroup(this.form.groupId.value)">
    
        <br/>
        <br/>
        GroupId:<input type="text" name="groupId2">
        <input type="button" value="离开客户端组" onclick="leaveGroup(this.form.groupId2.value)">
    
    
        <br/>
        <br/>
        SendToClientId:<input type="text" name="clientId">
        <textarea name="message1" style="width: 400px;height: 100px"></textarea>
        <input type="button" value="发送数据"
               onclick="sendSingleMessage(this.form.clientId.value,this.form.message1.value);">
    
        <br/>
        <br/>
        GroupId:<input type="text" name="groupId3">
        <textarea name="message2" style="width: 400px;height: 100px"></textarea>
        <input type="button" value="发送数据"
               onclick="sendGroupMessage(this.form.groupId3.value,this.form.message2.value);">
        <h3>回复消息：</h3>
    
        <textarea id="responseText" style="width: 400px;height: 300px;"></textarea>
    
        <input type="button" onclick="javascript:document.getElementById('responseText').value=''" value="清空数据">
    </form>
    </body>
    </html>
    ```
