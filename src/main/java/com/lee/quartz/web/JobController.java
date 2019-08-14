package com.lee.quartz.web;

import com.github.pagehelper.PageInfo;
import com.lee.quartz.common.Result;
import com.lee.quartz.entity.QuartzJob;
import com.lee.quartz.service.IJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bx
 */
@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {

    private IJobService jobService;
    private Result result;

    public JobController(IJobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/add")
    public Result save(QuartzJob quartz) {
        log.info("新增任务");
        result = jobService.saveJob(quartz);
        return result;
    }

    @PostMapping("/list")
    public PageInfo list(String jobName, Integer pageNo, Integer pageSize) {
        log.info("任务列表");
        PageInfo pageInfo = jobService.listQuartzJob(jobName, pageNo, pageSize);
        return pageInfo;
    }

    @PostMapping("/trigger")
    public Result trigger(String jobName, String jobGroup) {
        log.info("触发任务");
        result = jobService.triggerJob(jobName, jobGroup);
        return result;
    }

    @PostMapping("/pause")
    public Result pause(String jobName, String jobGroup) {
        log.info("停止任务");
        result = jobService.pauseJob(jobName, jobGroup);
        return result;
    }

    @PostMapping("/resume")
    public Result resume(String jobName, String jobGroup) {
        log.info("恢复任务");
        result = jobService.resumeJob(jobName, jobGroup);
        return result;
    }

    @PostMapping("/remove")
    public Result remove(String jobName, String jobGroup) {
        log.info("移除任务");
        result = jobService.removeJob(jobName, jobGroup);
        return result;
    }
}
