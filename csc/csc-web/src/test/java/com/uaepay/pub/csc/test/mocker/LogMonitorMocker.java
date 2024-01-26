package com.uaepay.pub.csc.test.mocker;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogRule;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogRuleMapper;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogStatMapper;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.LogRuleRepository;
import com.uaepay.pub.csc.test.dal.LogRuleTestMapper;

@Service
public class LogMonitorMocker {

    @Autowired
    LogRuleMapper logRuleMapper;

    @Autowired
    LogRuleTestMapper logRuleTestMapper;

    @Autowired
    LogRuleRepository logRuleRepository;

    @Autowired
    LogStatMapper logStatMapper;

    public void clearCgsLogRule() {
        List<LogRule> logRules = logRuleRepository.getLogRuleByCode("CGS_LOG_MONITOR");
        for (LogRule logRule : logRules) {
            logRuleTestMapper.deleteById(logRule.getId());
        }
    }

    public void mockLogRule(LogRule logRule) {
        logRuleMapper.insertSelective(logRule);
    }

    public LogStat mockLogStat(Date beginTime, Date endTime, String appCode, String apiCode, String returnCode,
        String returnMsg, Long count, String lastTid, String status, String updateBy) {
        LogStat logStat = new LogStat();
        logStat.setBeginTime(beginTime);
        logStat.setEndTime(endTime);
        logStat.setAppCode(appCode);
        logStat.setApiCode(apiCode);
        logStat.setReturnCode(returnCode);
        logStat.setReturnMsg(returnMsg);
        logStat.setValueCount(count);
        logStat.setLatestTid(lastTid);
        logStat.setStatus(status);
        logStat.setUpdateBy(updateBy);
        logStat.setCreateTime(DateTime.now().toDate());
        logStat.setUpdateTime(DateTime.now().toDate());
        logStatMapper.insertSelective(logStat);
        return logStat;
    }

    public void mockLogStat(LogStat logStat) {
        logStatMapper.insertSelective(logStat);
    }

}
