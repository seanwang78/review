package com.uaepay.pub.csc.cases.domainservice.monitor.schedule;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.cases.util.FileUtil;
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

public class MonitorScheduleExecuteEsTest extends MockTestBase {

    private static final int DEFAULT_CHECK_MINUTES = 60;
    private static final int DEFAULT_DELAY_MINUTES = 15;
    private static final String MONITOR_ALARM_STATUS_TEMP = "ut_ma_es";

    /** 基准时间 */
    private static final DateTime BASE_TIME, FIXED_TIME;

    static {
        // 基准时间
        BASE_TIME = new DateTime(2020, 3, 1, 0, 0, 0);
        FIXED_TIME = BASE_TIME.plusHours(3);
        DateTimeUtils.setCurrentMillisFixed(FIXED_TIME.getMillis());
    }

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

    /**
     * 正常执行对账计划，生成2个任务，一个失败，一个成功
     */
    @Test
    public void test_normal() {
        // mock定义，计划
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS).es()
            .alarm(TestConstants.MONITOR_ALARM_STATUS_ES_FIND, "orderNo").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2020-03-01 01:30:00");
        testDataMocker.mockEs();

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
            .fail(AlarmLevelEnum.NORMAL).recordDetailCount(1).detailChecker(0).identity("2").content(
                "_id=ut_ma_status_M_2_E, amount=0.00020, orderNo=2, updateTime=2020-02-29T21:30:00.000Z, status=E");
    }

    /**
     * 1:2:1
     */
    @Test
    public void test_normal_aggregations_1() throws IOException {

        String esSql = FileUtil.readToString("es_test/monitor_alarm_es_aggregation_1.json");
        // mock定义，计划
        long defineId = monitorMocker.mock(
            new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS).es().alarm(esSql, "group1").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock 12数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "S", "0.0001")
            .updateTime("2020-03-01 00:30:00");

        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "2", "E", "0.0002")
            .updateTime("2020-03-01 01:30:00");

        testDataMocker.monitorDataBuilder("province1", "city2", "district2", "3", "E", "3")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district2", "4", "E", "4")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district2", "5", "E", "5")
            .updateTime("2020-03-01 01:30:00");

        testDataMocker.mockEs();

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
            .fail(AlarmLevelEnum.NORMAL).recordDetailCount(2).detailChecker(0).identity("province1").content(
                "group1=province1, group2=city2, group3=district2, _count=3, max=5.0, min=3.0, maxDate=2020-02-29T21:30:00.000Z");

        scheduleChecker.taskChecker(1)
            .time(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES), BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .fail(AlarmLevelEnum.NORMAL).recordDetailCount(2).detailChecker(1).identity("province1").content(
                "group1=province1, group2=city1, group3=district1, _count=1, max=0.00020, min=0.00020, maxDate=2020-02-29T21:30:00.000Z");
    }

    /**
     * 1:2:3 6条记录
     */
    @Test
    public void test_normal_aggregations_2() throws IOException {

        String esSql = FileUtil.readToString("es_test/monitor_alarm_es_aggregation_1.json");
        // mock定义，计划
        long defineId = monitorMocker.mock(
            new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS).es().alarm(esSql, "group1").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock 12数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "S", "0.0001")
            .updateTime("2020-03-01 00:30:00");

        testDataMocker.monitorDataBuilder("province1", "city1", "district4", "8", "E", "9")
            .updateTime("2020-03-01 01:30:00");

        testDataMocker.monitorDataBuilder("province1", "city2", "district5", "9", "E", "10")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district5", "10", "E", "11")
            .updateTime("2020-03-01 01:30:00");

        testDataMocker.monitorDataBuilder("province1", "city2", "district6", "11", "E", "12")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district6", "12", "E", "13")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district6", "13", "E", "14")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district6", "14", "E", "15")
            .updateTime("2020-03-01 01:30:00");

        testDataMocker.mockEs();

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME.plusHours(2)).nextTrigger(BASE_TIME.plusHours(3).plusMinutes(15))
            .currentTaskNull().version(4).taskCount(2);
        taskChecker = scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .success().detailCount(0);

        taskChecker = scheduleChecker.taskChecker(1)
            .time(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES), BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .fail(AlarmLevelEnum.NORMAL).recordDetailCount(3);
        taskChecker.detailChecker(0).identity("province1").content(
            "group1=province1, group2=city2, group3=district6, _count=4, max=15.0, min=12.0, maxDate=2020-02-29T21:30:00.000Z");
        taskChecker.detailChecker(1).identity("province1").content(
            "group1=province1, group2=city2, group3=district5, _count=2, max=11.0, min=10.0, maxDate=2020-02-29T21:30:00.000Z");
        taskChecker.detailChecker(2).identity("province1").content(
            "group1=province1, group2=city1, group3=district4, _count=1, max=9.0, min=9.0, maxDate=2020-02-29T21:30:00.000Z");
    }

    /**
     * 没有terms,只有一层
     */
    @Test
    public void test_normal_aggregations_3() {
        String esSql = FileUtil.readToString("es_test/monitor_alarm_es_aggregation_3.json");
        // mock定义，计划
        long defineId = monitorMocker.mock(
            new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS).es().alarm(esSql, "count").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock 12数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "S", "0.0001")
            .updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "2", "E", "3")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district2", "3", "E", "4")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district2", "4", "E", "5")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.mockEs();

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .nextTrigger(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 3).plusMinutes(DEFAULT_DELAY_MINUTES))
            .currentTaskNull().version(4).taskCount(2);
        taskChecker = scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .success().detailCount(0);

        taskChecker = scheduleChecker.taskChecker(1)
            .time(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES), BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .fail(AlarmLevelEnum.NORMAL).recordDetailCount(1);
        taskChecker.detailChecker(0).identity("3")
            .content("max=5.0, min=3.0, maxDate=2020-02-29T21:30:00.000Z, count=3");
    }

    /**
     * metric使用format
     */
    @Test
    public void test_normal_aggregations_with_format() throws IOException {
        String esSql = FileUtil.readToString("es_test/monitor_alarm_es_aggregation_5_format.json");
        // mock定义，计划
        long defineId = monitorMocker.mock(
            new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS).es().alarm(esSql, "count").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES * 2, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock 12数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "E", "0.50")
            .updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district2", "3", "E", "6.00")
            .updateTime("2020-03-01 00:45:00");
        testDataMocker.mockEs();

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .nextTrigger(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 4).plusMinutes(DEFAULT_DELAY_MINUTES))
            .currentTaskNull().version(2).taskCount(1);
        taskChecker = scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .fail(AlarmLevelEnum.NORMAL).recordDetailCount(1);
        taskChecker.detailChecker(0).identity("2").content("max=6.0, min=0.5, maxDate=02-29 20:45+00, count=2");
    }

    /**
     * 没有aggs 没有内容
     */
    @Test
    public void test_error_bad_aggregations_4() throws IOException {

        String esSql = FileUtil.readToString("es_test/monitor_aggregation_error_1_empty_agg.json");
        // mock定义，计划
        long defineId = monitorMocker
            .mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS).es().alarm(esSql, "max").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock 12数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "S", "0.0001")
            .updateTime("2020-03-01 00:30:00");

        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "2", "E", "3")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district2", "3", "E", "4")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district2", "4", "E", "5")
            .updateTime("2020-03-01 01:30:00");

        testDataMocker.mockEs();

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .errorPrefixMessage("BAD_SQL", "invalid aggregation params").detailNull();
    }

    @Test
    public void test_error_index_not_exist() {
        // mock定义计划
        long defineId =
            monitorMocker.mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).es().alarm("{}", "orderNo"));
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
            .error("CONFIG_ERROR", "query template 'index' not exist").detailNull();
    }

    @Test
    public void test_error_query_param_not_exist() {
        // mock定义计划
        long defineId = monitorMocker
            .mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).es().alarm("{index: 'whatever'}", "orderNo"));
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
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).es()
            .alarm("{\"index\":\"i_dev_csc_test_data\", \"query\": {\"a\": {begin}, \"b\": {end}}}", "orderNo"));
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
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).errorPrefixMessage(
            "BAD_SQL",
            "Elasticsearch exception [type=parsing_exception, reason=[a] query malformed, no start_object after query name]")
            .detailNull();
    }

    @Test
    public void test_error_bad_sql_es1() {
        // mock定义
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).es()
            .alarm(TestConstants.MONITOR_ALARM_ES_AGGREGATION_BADSQL_1, "orderNo"));
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
            .errorPrefixMessage("INVALID_PARAMETER", "terms 相同层级只能有一个").detailNull();
    }

}
