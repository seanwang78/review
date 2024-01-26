package com.uaepay.pub.csc.core.dal.mapper.compare;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;

public interface CompareScheduleMapper {

    int insertSelective(CompareSchedule record);

    CompareSchedule selectByPrimaryKey(Long scheduleId);

    int updateByPrimaryKeySelective(CompareSchedule record);

    List<CompareSchedule> selectToExecute(@Param("beforeMinutes") int beforeMinutes,
        @Param("shardingIndex") int shardingIndex, @Param("shardingCount") int shardingCount,
        @Param("batchSize") int batchSize);

    int updateTaskApplied(@Param("scheduleId") long scheduleId, @Param("version") long version,
        @Param("taskId") long taskId, @Param("nextTriggerTime") Date nextTriggerTime);

    int updateTaskErrorDelay(@Param("scheduleId") long scheduleId, @Param("version") long version,
        @Param("errorCount") int errorCount, @Param("nextTriggerTime") Date nextTriggerTime);

    int updateTaskErrorReset(@Param("scheduleId") long scheduleId, @Param("version") long version,
        @Param("errorCount") int errorCount);

    int updateErrorStatus(@Param("scheduleId") long scheduleId, @Param("version") long version,
        @Param("errorCount") int errorCount, @Param("scheduleStatus") ScheduleStatusEnum scheduleStatus);

    int updateTaskNormal(@Param("scheduleId") long scheduleId, @Param("version") long version,
        @Param("errorCount") int errorCount, @Param("checkedTime") Date checkedTime,
        @Param("nextTriggerTime") Date nextTriggerTime);

}