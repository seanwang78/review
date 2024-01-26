package com.uaepay.pub.csc.core.dal.mapper.logmonitor;

import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogRule;

public interface LogRuleMapper {

    int insert(LogRule record);

    int insertSelective(LogRule record);

    List<LogRule> selectEnabledByFunctionCode(String functionCode);

    LogRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LogRule record);

    int updateByPrimaryKey(LogRule record);
}