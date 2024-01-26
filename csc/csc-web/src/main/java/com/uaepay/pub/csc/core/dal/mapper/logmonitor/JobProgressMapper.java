package com.uaepay.pub.csc.core.dal.mapper.logmonitor;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;

public interface JobProgressMapper {

    int insertSelective(JobProgress record);

    JobProgress selectByPrimaryKey(String jobCode);

    int updateByPrimaryKeySelective(JobProgress record);

    int updateProgress(JobProgress record);

    List<JobProgress> selectToExecute(@Param("beforeTime") Date beforeTime);
}