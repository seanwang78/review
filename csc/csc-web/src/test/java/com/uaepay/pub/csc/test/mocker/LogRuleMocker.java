package com.uaepay.pub.csc.test.mocker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogRule;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogRuleMapper;
import com.uaepay.pub.csc.service.facade.enums.LogRuleTypeEnum;
import com.uaepay.pub.csc.test.builder.LogRuleBuilder;
import com.uaepay.pub.csc.test.dal.LogRuleTestMapper;

@Service
public class LogRuleMocker {

    @Autowired
    LogRuleMapper logRuleMapper;

    @Autowired
    LogRuleTestMapper logRuleTestMapper;

    List<LogRuleBuilder> builders = new ArrayList<>();

    public LogRuleMocker reset() {
        this.builders.clear();
        return this;
    }

    public LogRuleBuilder cgsMonitorRule(LogRuleTypeEnum ruleType) {
        LogRuleBuilder builder = LogRuleBuilder.cgsMonitorRule(ruleType);
        builders.add(builder);
        return builder;
    }

    public void mock() {
        List<LogRule> logRules = new ArrayList<>();
        Set<String> functionCodeSet = new HashSet<>();
        for (LogRuleBuilder builder : builders) {
            LogRule logRule = builder.build();
            logRules.add(logRule);
            functionCodeSet.add(logRule.getFunctionCode());
        }
        // 删除数据
        for (String functionCode : functionCodeSet) {
            long delete = logRuleTestMapper.deleteByFunctionCode(functionCode);
            if (delete != 0) {
                System.out.printf("删除日志规则: %s, count=%d\n", functionCode, delete);
            }
        }
        // 保存数据
        for (LogRule logRule : logRules) {
            System.out.printf("mock数据: %s\n", logRule);
            logRuleMapper.insertSelective(logRule);
        }
    }

}
