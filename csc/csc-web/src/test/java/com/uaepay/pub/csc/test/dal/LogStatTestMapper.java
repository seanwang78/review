package com.uaepay.pub.csc.test.dal;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LogStatTestMapper {

    @Delete("delete from csc.t_log_stat")
    int deleteAll();


    @Select("select * from csc.t_log_stat order by id")
    @ResultMap("com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogStatMapper.BaseResultMap")
    List<LogStat> selectAll();
}
