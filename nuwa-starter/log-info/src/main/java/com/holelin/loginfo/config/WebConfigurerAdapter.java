package com.holelin.loginfo.config;

import com.holelin.loginfo.interceptor.LogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/1/6 16:37
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/1/6 16:37
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Configuration
public class WebConfigurerAdapter implements WebMvcConfigurer {
    @Bean
    public LogInterceptor logInterceptor() {
        return new LogInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor());
    }
}