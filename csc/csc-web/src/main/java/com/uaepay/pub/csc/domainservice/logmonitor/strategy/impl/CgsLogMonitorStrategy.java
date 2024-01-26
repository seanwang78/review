package com.uaepay.pub.csc.domainservice.logmonitor.strategy.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogRule;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.domain.properties.LogMonitorProperties;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.CgsLogRepository;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.LogRuleRepository;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.LogStatRepository;
import com.uaepay.pub.csc.domainservice.logmonitor.strategy.LogMonitorStrategy;
import com.uaepay.pub.csc.service.facade.enums.LogRuleExpressionTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum;
import com.uaepay.pub.csc.service.facade.enums.LogRuleTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author cyx
 */
@Slf4j
@Component
public class CgsLogMonitorStrategy implements LogMonitorStrategy {

    public static final String LOCK_KEY = "cgs_log_monitor_retry";

    @Autowired
    CgsLogRepository cgsLogRepository;

    @Autowired
    LogStatRepository logStatRepository;

    @Autowired
    LogRuleRepository logRuleRepository;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    LogMonitorProperties logMonitorProperties;

    @Override
    public String getCode() {
        return LogRuleFunctionCodeEnum.CGS_LOG_MONITOR.getCode();
    }

    @Override
    public void execute(Date beginDate, Date endDate) {
        List<LogStat> logStats = cgsLogRepository.statCgsLog(beginDate, endDate);
        List<LogRule> logRules = logRuleRepository.getLogRuleByCode(getCode());
        List<LogStat> result = filter(logRules, logStats);
        if (CollectionUtils.isNotEmpty(result)) {
            logStatRepository.saveLogStats(result);
        }
    }

    @Override
    public CommonResponse retry(Date beginDate, Date endDate, String operator) {
        // 加个锁
        RLock lock = redissonClient.getLock(LOCK_KEY);
        boolean locked = lock.tryLock();
        if (!locked) {
            // 未获得锁
            return CommonResponse.buildFail(CommonReturnCode.BIZ_CHECK_FAIL,
                "Retry is processing, please try again later.");
        }
        try {
            // 获得锁处理
            Long maxId = 0L;
            List<LogRule> logRules = logRuleRepository.getLogRuleByCode(getCode());
            int successCount = 0;
            while (true) {
                List<LogStat> logStats =
                    logStatRepository.selectToRetry(beginDate, endDate, maxId, logMonitorProperties.getQueryPageSize());
                log.info("query log stat from: {}, size: {}", maxId, logStats.size());
                if (logStats.size() == 0) {
                    break;
                }
                maxId = logStats.get(logStats.size() - 1).getId();

                // 重试过滤
                List<LogStat> result = filter(logRules, logStats);

                // 剔除重试依然有问题的，剩余的更新为重试成功
                logStats.removeAll(result);
                List<Long> idList = logStats.stream().map(LogStat::getId).collect(Collectors.toList());
                successCount += logStatRepository.updateRetrySuccess(idList, operator);
            }
            return CommonResponse.buildSuccess("Retry success count: " + successCount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public CommonResponse manualConfirm(List<Long> logStatIdList, String operator) {
        // 修改状态
        int success = logStatRepository.updateManualConfirm(logStatIdList, operator);
        int fail = logStatIdList.size() - success;
        String message = String.format("Update count success: %d, fail: %d", success, fail);
        return CommonResponse.buildSuccess(message);
    }

    private List<LogStat> filter(List<LogRule> logRules, List<LogStat> logStats) {
        List<LogStat> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(logStats)) {
            return result;
        }

        List<LogRule> blackList =
            logRules.stream().filter(logRule -> LogRuleTypeEnum.BLACK.getCode().equals(logRule.getRuleType()))
                .collect(Collectors.toList());

        List<LogRule> whiteList =
            logRules.stream().filter(logRule -> LogRuleTypeEnum.WHITE.getCode().equals(logRule.getRuleType()))
                .collect(Collectors.toList());

        // 黑白名单过滤 黑名单优先，
        for (LogStat logStat : logStats) {
            if (isMatchRuleList(blackList, logStat)) {
                result.add(logStat);
                continue;
            }
            if (!isMatchRuleList(whiteList, logStat)) {
                result.add(logStat);
            }
        }
        return result;
    }

    private boolean isMatchRuleList(List<LogRule> ruleList, LogStat logStat) {
        boolean isHit = false;
        for (LogRule logRule : ruleList) {
            LogRuleExpressionTypeEnum expressionType = LogRuleExpressionTypeEnum.getByCode(logRule.getExpressionType());
            if (isMatchCode(logRule.getReturnCode(), logStat.getReturnCode())
                && isMatchAppCode(logRule.getAppCode(), logStat.getAppCode())
                && isMatchApiCode(logRule.getApiCode(), logStat.getApiCode())
                && isMatchMsg(expressionType, logRule.getMatchExpression(), logStat.getReturnMsg())) {
                isHit = true;
                break;
            }
        }
        return isHit;
    }

    public static boolean isMatchAppCode(String ruleAppCode, String statAppCode) {
        return StringUtils.isBlank(ruleAppCode) || StringUtils.equals(ruleAppCode, statAppCode);
    }

    public static boolean isMatchApiCode(String ruleApiCode, String statApiCode) {
        return StringUtils.isBlank(ruleApiCode) || StringUtils.equals(ruleApiCode, statApiCode);
    }

    public static boolean isMatchCode(String ruleReturnCode, String statReturnCode) {
        return StringUtils.isBlank(ruleReturnCode) || StringUtils.equals(ruleReturnCode, statReturnCode);
    }

    public static boolean isMatchMsg(LogRuleExpressionTypeEnum expressionType, String expression, String returnMsg) {
        if (expressionType == LogRuleExpressionTypeEnum.NONE) {
            return true;
        } else if (expressionType == LogRuleExpressionTypeEnum.TEXT) {
            return StringUtils.equals(StringUtils.trimToNull(returnMsg), StringUtils.trimToNull(expression));
        } else if (expressionType == LogRuleExpressionTypeEnum.REGEX) {
            Pattern pattern = Pattern.compile(StringUtils.trimToEmpty(expression));
            return pattern.matcher(StringUtils.trimToEmpty(returnMsg)).matches();
        } else {
            return false;
        }
    }

}
