package com.holelin.loginfo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2023/1/6 16:38
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/1/6 16:38
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/do-test")
    public String doTest(@RequestParam("name") String name) throws InterruptedException {
        log.info("入参 name={}",name);
        testTrace();
        log.info("调用结束 name={}",name);
        return "Hello,"+name;
    }

    @Async
    @GetMapping("/do-test-async")
    public String doTestAsync(@RequestParam("name") String name) throws InterruptedException {
        log.info("入参 name={}",name);
        testTrace();
        log.info("调用结束 name={}",name);
        return "Hello,"+name;
    }
    private void testTrace(){
        log.info("这是一行info日志");
        log.error("这是一行error日志");
        testTrace2();
    }
    private void testTrace2(){
        log.info("这也是一行info日志");

    }

}
