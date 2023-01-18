package com.holelin.loginfo;

import com.holelin.loginfo.utils.ThreadMdcUtil;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/1/6 16:46
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/1/6 16:46
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public final class CustomThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    public CustomThreadPoolTaskExecutor() {
        super();
    }

    @Override
    public void execute(Runnable task) {
        super.execute(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }


    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }
}