package com.lee.quartz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lee.quartz.common.Result;
import com.lee.quartz.entity.QuartzJob;
import com.lee.quartz.mapper.JobMapper;
import com.lee.quartz.service.IJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bx
 */
@Service
@Slf4j
public class JobServiceImpl implements IJobService {

    private static final String TRIGGER_IDENTITY = "trigger";
    private static final String PARAM_NAME = "paramName";
    private static final String PARAM_VALUE = "paramValue";
    private static final String SCHEDULER_INSTANCE_NAME = "schedulerInstanceName";

    @Value("${spring.quartz.properties.org.quartz.scheduler.instanceName}")
    private String schedulerInstanceName;

    private Scheduler scheduler;
    private JobMapper jobMapper;

    //构造器注入
    public JobServiceImpl(Scheduler scheduler, JobMapper jobMapper) {
        this.scheduler = scheduler;
        this.jobMapper = jobMapper;
    }

    @Override
    public PageInfo listQuartzJob(String jobName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<QuartzJob> jobList = jobMapper.listJob(jobName);
        fillJobData(jobList);
        PageInfo pageInfo = new PageInfo(jobList);
        return pageInfo;
    }

    private void fillJobData(List<QuartzJob> jobList) {
        jobList.forEach(job -> {
            JobKey key = new JobKey(job.getJobName(), job.getJobGroup());
            try {
                JobDataMap jobDataMap = scheduler.getJobDetail(key).getJobDataMap();
                List<Map<String, Object>> jobDataParam = new ArrayList<>();
                jobDataMap.forEach((k, v) -> {
                    Map<String, Object> jobData = new LinkedHashMap<>(2);
                    jobData.put(PARAM_NAME, k);
                    jobData.put(PARAM_VALUE, v);
                    jobDataParam.add(jobData);
                });
                job.setJobDataParam(jobDataParam);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public Result saveJob(QuartzJob quartz) {
        try {
            //如果是修改  展示旧的 任务
            if (quartz.getOldJobGroup() != null && !"".equals(quartz.getOldJobGroup())) {
                JobKey key = new JobKey(quartz.getOldJobName(), quartz.getOldJobGroup());
                scheduler.deleteJob(key);
            }

            //构建job信息
            Class cls = Class.forName(quartz.getJobClassName());
            cls.newInstance();
            JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
                    quartz.getJobGroup()).withDescription(quartz.getDescription()).build();
            putDataMap(job, quartz);

            // 触发时间点
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression().trim());
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(TRIGGER_IDENTITY + quartz.getJobName(), quartz.getJobGroup())
                    .startNow().withSchedule(cronScheduleBuilder).build();
            //交由Scheduler安排触发
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
        return Result.ok();
    }

    @Override
    public Result triggerJob(String jobName, String jobGroup) {
        JobKey key = new JobKey(jobName, jobGroup);
        try {
            scheduler.triggerJob(key);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
        return Result.ok();
    }

    @Override
    public Result pauseJob(String jobName, String jobGroup) {
        JobKey key = new JobKey(jobName, jobGroup);
        try {
            scheduler.pauseJob(key);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
        return Result.ok();
    }

    @Override
    public Result resumeJob(String jobName, String jobGroup) {
        JobKey key = new JobKey(jobName, jobGroup);
        try {
            scheduler.resumeJob(key);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
        return Result.ok();
    }

    @Override
    public Result removeJob(String jobName, String jobGroup) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_IDENTITY + jobName, jobGroup);
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
            log.info("removeJob:" + JobKey.jobKey(jobName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
        return Result.ok();
    }

    private void putDataMap(JobDetail job, QuartzJob quartz) {

        // 将scheduler instanceName存入jobDataMap，方便业务job中进行数据库操作
        JobDataMap jobDataMap = job.getJobDataMap();
        jobDataMap.put(SCHEDULER_INSTANCE_NAME, schedulerInstanceName);

        List<Map<String, Object>> jobDataParam = quartz.getJobDataParam();
        if (jobDataParam == null || jobDataParam.isEmpty()) {
            return;
        }
        jobDataParam.forEach(jobData -> jobDataMap.put(String.valueOf(jobData.get(PARAM_NAME)), jobData.get(PARAM_VALUE)));
    }
}