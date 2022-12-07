package cn.holelin.logfilter.starter.config;

import cn.holelin.logfilter.starter.filter.LogFilter;
import cn.holelin.logfilter.starter.util.LogFilterRegistrationBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/10/13 18:36
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/10/13 18:36
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Configuration
@ConditionalOnClass({LogFilterRegistrationBean.class, LogFilter.class})
public class LogFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogFilterRegistrationBean.class)
    public LogFilterRegistrationBean logFilterRegistrationBean() {
        return new LogFilterRegistrationBean();
    }
}
