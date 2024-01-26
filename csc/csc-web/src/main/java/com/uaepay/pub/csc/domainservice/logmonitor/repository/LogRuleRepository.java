package com.uaepay.pub.csc.domainservice.logmonitor.repository;

import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogRule;

/**
 * @author zc
 */
public interface LogRuleRepository {

    /**
     * 查询规则列表
     * 
     * @param functionCode
     *            功能代码
     * @return 规则列表
     */
    List<LogRule> getLogRuleByCode(String functionCode);

}
