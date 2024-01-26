package com.uaepay.pub.csc.test.builder;

import java.util.Date;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogRule;
import com.uaepay.pub.csc.service.facade.enums.LogRuleExpressionTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum;
import com.uaepay.pub.csc.service.facade.enums.LogRuleTypeEnum;

public class LogRuleBuilder {

    public static LogRuleBuilder cgsMonitorRule(LogRuleTypeEnum ruleType) {
        return new LogRuleBuilder(LogRuleFunctionCodeEnum.CGS_LOG_MONITOR, ruleType);
    }

    LogRule result = new LogRule();

    public LogRuleBuilder(LogRuleFunctionCodeEnum functionCode, LogRuleTypeEnum ruleType) {
        result.setFunctionCode(functionCode.getCode());
        result.setRuleType(ruleType.getCode());
        result.setEnableFlag(YesNoEnum.YES.getCode());
        result.setCreateTime(new Date());
        result.setUpdateTime(new Date());
        allAppCode().allApiCode().allCode().noneExpression();
    }

    public LogRuleBuilder appCode(String appCode) {
        result.setAppCode(appCode);
        return this;
    }

    public LogRuleBuilder allAppCode() {
        result.setAppCode(null);
        return this;
    }

    public LogRuleBuilder apiCode(String apiCode) {
        result.setApiCode(apiCode);
        return this;
    }

    public LogRuleBuilder allApiCode() {
        result.setApiCode(null);
        return this;
    }

    public LogRuleBuilder code(String returnCode) {
        result.setReturnCode(returnCode);
        return this;
    }

    public LogRuleBuilder allCode() {
        result.setReturnCode(null);
        return this;
    }

    public LogRuleBuilder noneExpression() {
        result.setExpressionType(LogRuleExpressionTypeEnum.NONE.getCode());
        return this;
    }

    public LogRuleBuilder textExpression(String expression) {
        result.setExpressionType(LogRuleExpressionTypeEnum.TEXT.getCode());
        result.setMatchExpression(expression);
        return this;
    }

    public LogRuleBuilder regexExpression(String expression) {
        result.setExpressionType(LogRuleExpressionTypeEnum.REGEX.getCode());
        result.setMatchExpression(expression);
        return this;
    }

    public LogRuleBuilder disabled() {
        result.setEnableFlag(YesNoEnum.NO.getCode());
        return this;
    }

    public LogRule build() {
        return result;
    }

}
