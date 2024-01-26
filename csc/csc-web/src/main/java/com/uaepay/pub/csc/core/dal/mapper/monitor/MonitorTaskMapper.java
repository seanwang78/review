package com.uaepay.pub.csc.core.dal.mapper.monitor;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.service.facade.enums.MonitorTaskStatusEnum;

public interface MonitorTaskMapper {

    int insertSelective(MonitorTask record);

    MonitorTask selectByPrimaryKey(Long taskId);

    int updateSuccess(@Param("task") MonitorTask task, @Param("newStatus") MonitorTaskStatusEnum newStatus);

    int updateError(@Param("task") MonitorTask task, @Param("newStatus") MonitorTaskStatusEnum newStatus);

    int updateFail(@Param("task") MonitorTask task, @Param("newStatus") MonitorTaskStatusEnum newStatus);

    int updateLastRetryTask(@Param("origTaskId") long origTaskId, @Param("retryTaskId") long retryTaskId);

    int updateRetrySuccess(@Param("task") MonitorTask task, @Param("newStatus") MonitorTaskStatusEnum newStatus);

    int updateManualConfirmed(@Param("task") MonitorTask task, @Param("newStatus") MonitorTaskStatusEnum newStatus);

    int updateManualConfirmedBatch(@Param("taskIds") List<Long> taskIds, @Param("errorMessage") String errorMessage);

    int deleteByPrimaryKey(Long taskId);

    int insert(MonitorTask record);

    int updateByPrimaryKeySelective(MonitorTask record);

    int updateByPrimaryKey(MonitorTask record);
}