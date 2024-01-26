package com.uaepay.pub.csc.core.dal.mapper.logmonitor;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;

public interface LogStatMapper {

    int insertSelective(LogStat record);

    LogStat selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LogStat record);

    List<LogStat> selectToRetry(@Param("beginTime") Date begin, @Param("endTime") Date end, @Param("id") Long startId,
        @Param("pageSize") Integer pageSize);

    int updateStatus(@Param("idList") List<Long> idList, @Param("status") String status,
        @Param("operator") String operator);

}