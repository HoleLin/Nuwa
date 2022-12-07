package cn.holelin.logfilter.starter.util;

import cn.holelin.logfilter.starter.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/10/13 17:41
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/10/13 17:41
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class LogFilterRegistrationBean extends FilterRegistrationBean<LogFilter> {

    public LogFilterRegistrationBean() {
        super();
        // 添加LogFilter过滤器
        this.setFilter(new LogFilter());
        // 匹配所有路径
        this.addUrlPatterns("/*");
        // 定义过滤器名
        this.setName("LogFilter");
        // 设置优先级
        this.setOrder(1);
    }
}
