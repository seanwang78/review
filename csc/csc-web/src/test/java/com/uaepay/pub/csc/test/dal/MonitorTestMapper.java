package com.uaepay.pub.csc.test.dal;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTaskDetail;

/**
 * @author zc
 */
public interface MonitorTestMapper {

    @Select("select define_id from csc.t_monitor_define where define_name = #{defineName}")
    Long selectDefineByName(@Param("defineName") String defineName);

    @Delete("delete from csc.t_monitor_define where define_id = #{defineId}")
    int deleteDefineById(@Param("defineId") long defineId);

    @Delete("delete from csc.t_monitor_schedule where define_id = #{defineId}")
    int deleteScheduleByDefineId(@Param("defineId") long defineId);

    @Select("select * from csc.t_monitor_task where task_type = 'S' and operator = 'system' and memo = #{memo} order by create_time")
    @ResultMap("com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorTaskMapper.BaseResultMap")
    List<MonitorTask> selectTaskBySchedule(@Param("memo") String memo);

    @Select("select * from csc.t_monitor_task_detail where task_id = #{taskId} order by detail_id")
    @ResultMap("com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorTaskDetailMapper.BaseResultMap")
    List<MonitorTaskDetail> selectDetailByTaskId(@Param("taskId") long taskId);

}
