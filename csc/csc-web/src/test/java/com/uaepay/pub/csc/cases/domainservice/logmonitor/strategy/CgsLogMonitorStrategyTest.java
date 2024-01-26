package com.uaepay.pub.csc.cases.domainservice.logmonitor.strategy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.pub.csc.domainservice.logmonitor.strategy.impl.CgsLogMonitorStrategy;
import com.uaepay.pub.csc.service.facade.enums.LogRuleExpressionTypeEnum;
import com.uaepay.pub.csc.test.base.TestBase;

public class CgsLogMonitorStrategyTest extends TestBase {

    @Test
    public void testIsMatchCode() {
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchCode(null, WHATEVER));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchCode("", WHATEVER));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchCode("  ", WHATEVER));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchCode(null, null));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchCode("", "  "));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchCode("  ", ""));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchCode(WHATEVER, WHATEVER));

        Assertions.assertFalse(CgsLogMonitorStrategy.isMatchCode("1", WHATEVER));
        Assertions.assertFalse(CgsLogMonitorStrategy.isMatchCode(WHATEVER + "1", WHATEVER));
        Assertions.assertFalse(CgsLogMonitorStrategy.isMatchCode(WHATEVER, WHATEVER + "1"));
    }

    @Test
    public void testNoneExpression() {
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.NONE, null, WHATEVER));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.NONE, null, null));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.NONE, null, ""));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.NONE, null, "  "));
    }

    @Test
    public void testTextExpression() {
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, null, null));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, null, ""));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, null, " "));

        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, "", null));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, "", ""));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, "", " "));

        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, "  ", null));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, "  ", ""));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, "  ", " "));

        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, WHATEVER, WHATEVER));
        Assertions
            .assertFalse(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, WHATEVER, WHATEVER + "1"));
        Assertions
            .assertFalse(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, WHATEVER + "1", WHATEVER));
        Assertions.assertFalse(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.TEXT, "ABC", "abc"));
    }

    @Test
    public void testRegexExpression() {
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, "\\s*", null));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, "\\s*", ""));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, "\\s*", "  "));

        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, "abc", "abc"));
        Assertions.assertFalse(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, "abc", "abc def"));

        Assertions
            .assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, ".*123.*", "aaa123bbb"));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, ".*123.*", "aaa123"));
        Assertions.assertTrue(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, ".*123.*", "123bbb"));
        Assertions.assertFalse(CgsLogMonitorStrategy.isMatchMsg(LogRuleExpressionTypeEnum.REGEX, ".*123.*", "abc"));
    }

}
