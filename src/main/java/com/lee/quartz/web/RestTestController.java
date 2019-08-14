package com.lee.quartz.web;

import com.lee.quartz.entity.TestEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
@Slf4j
public class RestTestController {
    @RequestMapping("/getEntity")
    public TestEntity getEntity() {
        log.info("收到请求...");
        return new TestEntity("zhangsan", 22);
    }

}
