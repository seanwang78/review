package com.uaepay.pub.csc.cases.domainservice.monitor.schedule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import com.uaepay.pub.csc.domain.properties.ScheduleProperties;
import com.uaepay.pub.csc.domainservice.monitor.schedule.MonitorScheduleExecuteService;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.builder.MonitorScheduleBuilder;
import com.uaepay.pub.csc.test.checker.monitor.MonitorScheduleCheckerFactory;
import com.uaepay.pub.csc.test.checker.monitor.MonitorTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class MonitorScheduleExecuteMongoTest extends MockTestBase {

    private static final int DEFAULT_CHECK_MINUTES = 60;
    private static final int DEFAULT_DELAY_MINUTES = 15;
    private static final String MONITOR_ALARM_STATUS_TEMP = "ut_ma_mongo";

    /** 基准时间 */
    private static DateTime BASE_TIME, FIXED_TIME;

    @Autowired
    MonitorMocker monitorMocker;

    @Autowired
    TestDataMocker testDataMocker;

    @Autowired
    MonitorScheduleExecuteService scheduleExecuteService;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    @Autowired
    MonitorScheduleCheckerFactory monitorScheduleCheckerFactory;

    @Autowired
    ScheduleProperties scheduleProperties;

    MonitorTaskCheckerFactory.TaskChecker taskChecker;

    @BeforeEach
    public void init() {
        // 基准时间
        BASE_TIME = new DateTime(2020, 3, 1, 0, 0, 0);
        FIXED_TIME = BASE_TIME.plusHours(3);
        DateTimeUtils.setCurrentMillisFixed(FIXED_TIME.getMillis());
    }

    /**
     * 正常执行对账计划，生成2个任务，一个失败，一个成功
     */
    @Test
    public void test_normal() {
        // mock定义，计划
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS).mongo()
            .alarm(TestConstants.MONITOR_ALARM_STATUS_MONGO_FIND, "orderNo").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2020-03-01 01:30:00");
        testDataMocker.mockMongo();

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME.plusHours(2)).nextTrigger(BASE_TIME.plusHours(3).plusMinutes(15))
            .currentTaskNull().version(4).taskCount(2);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).success()
            .detailCount(0);
        scheduleChecker.taskChecker(1)
            .time(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES), BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .fail(AlarmLevelEnum.NORMAL).recordDetailCount(1).detailChecker(0).identity("2")
            .content("orderNo=2, status=E, amount=0.0002, updateTime=2020-03-01T01:30:00+04");
    }

    /**
     * 正常执行对账计划，结果字段不同，且分页
     */
    @Test
    public void test_normal_with_different_fields() {
        // mock定义，计划
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS).mongo()
            .alarm(TestConstants.MONITOR_ALARM_STATUS_MONGO_FIND_DIFFERENT_FIELDS, "orderNo").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.01").updateTime("2020-03-01 00:10:00").currency("AED");
        testDataMocker.monitorDataBuilder("2", "E", "0.02").updateTime("2020-03-01 00:20:00").currency("AED");
        testDataMocker.monitorDataBuilder("3", "E", "0.03").updateTime("2020-03-01 00:30:00").group1("g1");
        testDataMocker.mockMongo();

        // 执行计划
        DateTimeUtils.setCurrentMillisFixed(
            BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES).plusMinutes(DEFAULT_DELAY_MINUTES).getMillis());
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .nextTrigger(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2).plusMinutes(DEFAULT_DELAY_MINUTES))
            .currentTaskNull().version(2).taskCount(1);
        taskChecker = scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .fail(AlarmLevelEnum.NORMAL).recordDetailCount(3);
        taskChecker.detailChecker(0).identity("1")
            .content("orderNo=1, status=E, amount=0.01, currency=AED, updateTime=2020-03-01T00:10:00+04, group1=null");
        taskChecker.detailChecker(1).identity("2")
            .content("orderNo=2, status=E, amount=0.02, currency=AED, updateTime=2020-03-01T00:20:00+04, group1=null");
        taskChecker.detailChecker(2).identity("3")
            .content("orderNo=3, status=E, amount=0.03, currency=null, updateTime=2020-03-01T00:30:00+04, group1=g1");
    }

    @Test
    public void test_error_db_not_exist() {
        // mock定义计划
        long defineId =
            monitorMocker.mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).mongo().alarm("{}", "orderNo"));
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("CONFIG_ERROR", "query template 'db' not exist").detailNull();
    }

    @Test
    public void test_error_query_param_not_exist() {
        // mock定义计划
        long defineId = monitorMocker
            .mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).mongo().alarm("{db: 'testdb'}", "orderNo"));
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("INVALID_PARAMETER", "{begin} not exist").detailNull();
    }

    @Test
    public void test_error_bad_sql() {
        // mock定义
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).mongo()
            .alarm("{db: 'testdb', find: 'xxx', a: {begin}, b: {end}}", "orderNo"));
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .errorPrefixMessage("BAD_SQL", "Command failed with error 9 (FailedToParse): 'Failed to parse: ")
            .detailNull();
    }

}
