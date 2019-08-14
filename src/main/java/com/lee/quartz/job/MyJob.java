package com.lee.quartz.job;

import com.lee.quartz.entity.TestEntity1;
import com.lee.quartz.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author baixin
 */
@Slf4j
public class MyJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String url = "http://localhost:9001/quartz/rest/getEntity";
        TestEntity1 entity = RestTemplateUtil.getRequest(url, TestEntity1.class);
        log.info("entity = {}", entity);
        log.info("MyJob...");
    }
}
