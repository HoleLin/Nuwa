package cn.holelin.dicom.pacs_v1.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HoleLin
 */
@Configuration
public class PacsConfiguration {

    /**
     * 处理拉图任务线程池
     *
     * @return 线程池
     */
    @Bean
    public ExecutorService pullTaskExecutorService() {
        return new ThreadPoolExecutor(5, 5,
                10L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5), new ThreadFactoryBuilder().setNameFormat("pacs-pull-task-handlers-%d").build());
    }

}
