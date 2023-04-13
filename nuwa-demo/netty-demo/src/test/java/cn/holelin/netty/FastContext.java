package cn.holelin.netty;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * Netty FastThreadLocal保存的本地变量
 * 只有在 FastThreadLocalThread 线程中调用才会使用FastThreadLocal的逻辑，否则会使用ThreadLocal
 */
public class FastContext {

    private static final FastThreadLocal<FastContext> FAST_THREAD_LOCAL = new FastThreadLocal<FastContext>() {
        @Override
        protected FastContext initialValue() {
            return new FastContext();
        }
    };

    public static FastContext currentContext() {
        return FAST_THREAD_LOCAL.get();
    }

    public static void remove() {
        FAST_THREAD_LOCAL.remove();
    }

    /**
     * 线程追踪ID
     */
    private String thraceId;

    public String getThraceId() {
        return thraceId;
    }

    public void setThraceId(String thraceId) {
        this.thraceId = thraceId;
    }

}


