package cn.holelin.netty;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.UUID;

public class FastThreadLocalTest {
    public static void main(String[] args) {
        // Netty提供的DefaultThreadFactory创建的线程是FastThreadLocalThread类型的
        DefaultThreadFactory NettyFastThreadFactory = new DefaultThreadFactory("FastThreadTest");
        NettyFastThreadFactory.newThread(() -> {
            FastContext fastContext = FastContext.currentContext();
            String uuid = UUID.randomUUID().toString();
            System.out.println(uuid);
            fastContext.setThraceId(uuid);

            testFastThreadLocal();
        }).start();

    }

    private static void testFastThreadLocal() {
        FastContext fastContext = FastContext.currentContext();
        System.out.println(fastContext.getThraceId());
        FastContext.remove();
    }
}
