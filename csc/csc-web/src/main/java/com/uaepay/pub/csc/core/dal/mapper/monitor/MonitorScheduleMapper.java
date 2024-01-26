package com.uaepay.pub.csc.core.dal.mapper.monitor;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;

public interface MonitorScheduleMapper {

    int insertSelective(MonitorSchedule record);

    MonitorSchedule selectByPrimaryKey(Long scheduleId);

    int updateByPrimaryKeySelective(MonitorSchedule record);

    List<MonitorSchedule> selectToExecute(@Param("beforeMinutes") int beforeMinutes,
        @Param("shardingIndex") int shardingIndex, @Param("shardingCount") int shardingCount,
        @Param("batchSize") int batchSize);

    int updateTaskApplied(@Param("scheduleId") long scheduleId, @Param("updateVersion") long updateVersion,
        @Param("taskId") long taskId, @Param("nextTriggerTime") Date nextTriggerTime);

    int updateTaskErrorDelay(@Param("scheduleId") long scheduleId, @Param("updateVersion") long updateVersion,
        @Param("errorCount") int errorCount, @Param("nextTriggerTime") Date nextTriggerTime);

    int updateTaskErrorReset(@Param("scheduleId") long scheduleId, @Param("updateVersion") long updateVersion,
        @Param("errorCount") int errorCount);

    int updateErrorStatus(@Param("scheduleId") long scheduleId, @Param("updateVersion") long updateVersion,
        @Param("errorCount") int errorCount, @Param("scheduleStatus") ScheduleStatusEnum scheduleStatus);

    int updateTaskNormal(@Param("scheduleId") long scheduleId, @Param("updateVersion") long updateVersion,
        @Param("errorCount") int errorCount, @Param("checkedTime") Date checkedTime,
        @Param("nextTriggerTime") Date nextTriggerTime);
}