package cn.holelin.common.utils;

import cn.holelin.logfilter.starter.EnableLogFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableLogFilter
@SpringBootApplication
public class CommonUtilsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonUtilsApplication.class, args);
    }

}
