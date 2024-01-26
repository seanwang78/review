package com.uaepay.pub.csc.cases.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.pub.csc.service.facade.enums.*;
import com.uaepay.pub.csc.test.base.TestConstants;

public class EnumTest implements TestConstants {

    @Test
    public void test() {
        Assertions.assertEquals(DataSourceTypeEnum.API, DataSourceTypeEnum.getByCode("API"));
        Assertions.assertNull(DataSourceTypeEnum.getByCode(WHATEVER));

        Assertions.assertEquals(NotifyTypeEnum.SMS, NotifyTypeEnum.getByCode("SMS"));
        Assertions.assertNull(NotifyTypeEnum.getByCode(WHATEVER));

        Assertions.assertEquals(PriorityEnum.HIGH, PriorityEnum.getByCode("0"));
        Assertions.assertNull(PriorityEnum.getByCode(WHATEVER));

        Assertions.assertEquals(ScheduleStatusEnum.ERROR, ScheduleStatusEnum.getByCode("E"));
        Assertions.assertNull(ScheduleStatusEnum.getByCode(WHATEVER));

        Assertions.assertEquals(SplitStrategyEnum.UNION, SplitStrategyEnum.getByCode("UNION"));
        Assertions.assertNull(SplitStrategyEnum.getByCode(WHATEVER));

        Assertions.assertEquals(TaskStatusEnum.ERROR, TaskStatusEnum.getByCode("E"));
        Assertions.assertNull(TaskStatusEnum.getByCode(WHATEVER));

        Assertions.assertEquals(TaskTypeEnum.MANUAL, TaskTypeEnum.getByCode("M"));
        Assertions.assertNull(TaskTypeEnum.getByCode(WHATEVER));
    }

    @Test
    public void compareEnumTest() {
        Assertions.assertEquals(CompareStatusEnum.LACK, CompareStatusEnum.getByCode("L"));
        Assertions.assertNull(CompareStatusEnum.getByCode(WHATEVER));

        Assertions.assertEquals(CompensateFlagEnum.ERROR, CompensateFlagEnum.getByCode("E"));
        Assertions.assertNull(CompensateFlagEnum.getByCode(WHATEVER));
    }

    @Test
    public void monitorEnumTest() {
        Assertions.assertEquals(AlarmLevelEnum.IGNORE, AlarmLevelEnum.getByCode("I"));
        Assertions.assertNull(AlarmLevelEnum.getByCode(WHATEVER));

        Assertions.assertEquals(MonitorTaskStatusEnum.ERROR, MonitorTaskStatusEnum.getByCode("E"));
        Assertions.assertNull(MonitorTaskStatusEnum.getByCode(WHATEVER));

        Assertions.assertEquals(MonitorTypeEnum.ALARM, MonitorTypeEnum.getByCode("A"));
        Assertions.assertNull(MonitorTypeEnum.getByCode(WHATEVER));
    }

    @Test
    public void logMonitorEnumTest() {
        Assertions.assertEquals(LogRuleExpressionTypeEnum.TEXT, LogRuleExpressionTypeEnum.getByCode("TEXT"));
        Assertions.assertNull(LogRuleExpressionTypeEnum.getByCode(WHATEVER));

        Assertions.assertEquals(LogRuleFunctionCodeEnum.CGS_LOG_MONITOR,
            LogRuleFunctionCodeEnum.getByCode("CGS_LOG_MONITOR"));
        Assertions.assertNull(LogRuleFunctionCodeEnum.getByCode(WHATEVER));

        Assertions.assertEquals(LogRuleTypeEnum.BLACK, LogRuleTypeEnum.getByCode("BLACK"));
        Assertions.assertNull(LogRuleTypeEnum.getByCode(WHATEVER));

        Assertions.assertEquals(LogStatStatusEnum.INIT, LogStatStatusEnum.getByCode("I"));
        Assertions.assertNull(LogStatStatusEnum.getByCode(WHATEVER));
    }

}
