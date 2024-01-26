package com.uaepay.pub.csc.domainservice.logmonitor.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogRule;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogRuleMapper;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.LogRuleRepository;

/**
 * @author zc
 */
@Repository
public class LogRuleRepositoryImpl implements LogRuleRepository {

    @Autowired
    LogRuleMapper logRuleMapper;

    @Override
    public List<LogRule> getLogRuleByCode(String functionCode) {
        return logRuleMapper.selectEnabledByFunctionCode(functionCode);
    }

}
