package com.lee.quartz.mapper;

import com.lee.quartz.entity.QuartzJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobMapper {

    List<QuartzJob> listJob(@Param("jobName") String jobName);
}
