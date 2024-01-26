package com.uaepay.pub.csc.cases.domainservice.compare.schedule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.domain.properties.ScheduleProperties;
import com.uaepay.pub.csc.domainservice.compare.schedule.CompareScheduleExecuteService;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.builder.CompareScheduleBuilder;
import com.uaepay.pub.csc.test.checker.compare.CompareScheduleCheckerFactory;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.CompareDefineMocker;
import com.uaepay.pub.csc.test.mocker.CompareScheduleMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

/**
 * 未测试完
 * 
 * @author zc
 */
// @Disabled
public class CompareScheduleExecuteEsTest extends MockTestBase {

    private static final String SCHEDULE_NORMAL_FIND = "unittest-es_find";
    private static final String SCHEDULE_ERROR = "unittest-es_error";

    private static final int DEFAULT_SPLIT_MINUTES = 30;
    private static final int DEFAULT_CHECK_MINUTES = 60;
    private static final int DEFAULT_DELAY_MINUTES = 15;

    @Autowired
    CompareDefineMocker compareDefineMocker;

    @Autowired
    CompareScheduleMocker compareScheduleMocker;

    @Autowired
    TestDataMocker testDataMocker;

    @Autowired
    CompareScheduleExecuteService compareScheduleExecuteService;

    @Autowired
    CompareScheduleCheckerFactory compareScheduleCheckerFactory;

    @Autowired
    ScheduleProperties scheduleProperties;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    /** 基准时间 */
    private static final DateTime BASE_TIME, FIXED_TIME;

    private long defineNormal;

    static {
        // 基准时间
        BASE_TIME = new DateTime(2020, 12, 10, 0, 0, 0);
        FIXED_TIME = BASE_TIME.plusHours(3);
        DateTimeUtils.setCurrentMillisFixed(FIXED_TIME.getMillis());
    }

    @BeforeAll
    public void setUp() {
        // mock公共对账定义
        CompareDefineBuilder defineBuilder;
        // 正常定义
        defineBuilder = new CompareDefineBuilder(SCHEDULE_NORMAL_FIND);
        defineBuilder.src(TestConstants.DATASOURCE_ES, "orderNo", DEFAULT_SPLIT_MINUTES,
            "{\"index\": \"i_dev_csc_test_data\""
                + ", \"query\": { \"bool\": { \"filter\": [ {\"match\": {\"dataSet\": \"" + SCHEDULE_NORMAL_FIND
                + "\"}}" + ", {\"match\": {\"dataType\": \"S\"}}"
                + ", {\"range\": {\"updateTime\": {\"gte\": {begin}, \"lt\": {end}}}} ]}}"
                + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"], \"sort\": {\"updateTime\": \"asc\"}}");
        defineBuilder.target(TestConstants.DATASOURCE_ES, "orderNo",
            "{\"index\": \"i_dev_csc_test_data\"" + ", \"query\": {\"bool\": {\"filter\": [{\"match\": {\"dataSet\": \""
                + SCHEDULE_NORMAL_FIND + "\"}}"
                + ", {\"match\": {\"dataType\": \"T\"}}, {\"terms\": {\"orderNo\": {id_s}}} ]}}"
                + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"] }");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "$data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n" + " $data.isEqual('amount', 'amount')");
        defineNormal = compareDefineMocker.mock(defineBuilder);
    }

    @Test
    public void test_normal() {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineNormal, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);
        // mock数据
        testDataMocker.reset(SCHEDULE_NORMAL_FIND);
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime(BASE_TIME.plusMinutes(10));
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.67")
            .updateTime(BASE_TIME.plusHours(1).plusMinutes(20));
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime(BASE_TIME.plusHours(1).plusMinutes(30));
        testDataMocker.targetDataBuilder("a", "Success", "0.0001");
        testDataMocker.targetDataBuilder("b", "Fail", "123456789012345.67");
        testDataMocker.targetDataBuilder("c", "Failure", "0.01");
        testDataMocker.mockEs();
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME.plusHours(2)).nextTrigger(BASE_TIME.plusHours(3).plusMinutes(15))
            .currentTaskNull().version(4).taskCount(2);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).success("pass: 1")
            .detailCount(0);
        scheduleChecker.taskChecker(1)
            .time(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES), BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .success("pass: 2").detailCount(0);
    }

    @Test
    public void test_fail() throws InterruptedException {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineNormal, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);
        // mock数据
        testDataMocker.reset(SCHEDULE_NORMAL_FIND);
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime(BASE_TIME.plusMinutes(10));
        testDataMocker.srcDataBuilder("b", "F", "0.02").updateTime(BASE_TIME.plusHours(1).plusMinutes(20));
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime(BASE_TIME.plusHours(1).plusMinutes(30));
        testDataMocker.targetDataBuilder("a", "Success", "0.0001").updateTime(BASE_TIME);
        testDataMocker.targetDataBuilder("b", "Process", "0.03").updateTime(BASE_TIME);
        testDataMocker.mockEs();
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();
        Thread.sleep(5000);

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .nextTrigger(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 3).plusMinutes(DEFAULT_DELAY_MINUTES))
            .currentTaskNull().version(4).taskCount(2);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).success("pass: 1")
            .detailCount(0);

        CompareTaskCheckerFactory.TaskChecker taskChecker2 = scheduleChecker.taskChecker(1);
        taskChecker2
            .time(BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES), BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES * 2))
            .fail("pass: 0, lack: 1, mismatch: 1").detailCount(2);
        taskChecker2.detailChecker(0).identity("c").lackTarget(
            "_id=unittest-es_find_S_c_F, amount=0.01, orderNo=c, updateTime=2020-12-09T21:30:00.000Z, status=F");
        taskChecker2.detailChecker(1).identity("b").mismatch(
            "_id=unittest-es_find_S_b_F, amount=0.02, orderNo=b, updateTime=2020-12-09T21:20:00.000Z, status=F",
            "_id=unittest-es_find_T_b_Process, amount=0.03, orderNo=b, updateTime=2020-12-09T20:00:00.000Z, status=Process",
            "status[F] and status[Process] not correspond, rule: [F] -> [Fail,Failure], amount[0.02] and amount[0.03] not equal");
    }

    /**
     * 查询指令无法解析
     */
    @Test
    public void test_error_query_command() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_ES, "orderNo", DEFAULT_SPLIT_MINUTES, "whatever");
        defineBuilder.target(TestConstants.DATASOURCE_ES, "orderNo", "whatever");
        defineBuilder.compare("whatever");
        long defineId = compareDefineMocker.mock(defineBuilder);
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineId, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("CONFIG_ERROR", "source sql error").detailCount(0);
    }

    @Test
    public void test_error_source_sql_index_not_exist() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_ES, "orderNo", DEFAULT_SPLIT_MINUTES, "{}");
        defineBuilder.target(TestConstants.DATASOURCE_ES, "orderNo", "whatever");
        defineBuilder.compare("whatever");
        long defineId = compareDefineMocker.mock(defineBuilder);
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineId, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("CONFIG_ERROR", "source sql 'index' not exist").detailCount(0);
    }

    @Test
    public void test_error_source_sql_param_not_exist() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_ES, "orderNo", DEFAULT_SPLIT_MINUTES, "{index:'whatever'}");
        defineBuilder.target(TestConstants.DATASOURCE_ES, "orderNo", "whatever");
        defineBuilder.compare("whatever");
        long defineId = compareDefineMocker.mock(defineBuilder);
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineId, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("INVALID_PARAMETER", "{begin} not exist").detailCount(0);
    }

    @Test
    public void test_error_target_sql_param_not_exist() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_ES, "orderNo", DEFAULT_SPLIT_MINUTES,
            "{index:'whatever', a: {begin}, b: {end}}");
        defineBuilder.target(TestConstants.DATASOURCE_ES, "orderNo", "{}");
        defineBuilder.compare("whatever");
        long defineId = compareDefineMocker.mock(defineBuilder);
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineId, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();
        // waitFinish(10000);

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("INVALID_PARAMETER", "{id_s} or {id_n} not exist").detailCount(0);
    }

    /** 字段不存在 */
    @Test
    public void test_error_source_bad_request() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_ES, "orderNo", DEFAULT_SPLIT_MINUTES,
            "{\"index\":\"i_dev_csc_test_data\", \"query\": {\"a\": {begin}, \"b\": {end}}}");
        defineBuilder.target(TestConstants.DATASOURCE_ES, "orderNo", "{index: 'i_dev_csc_test_data', a: {id_s}}");
        defineBuilder.compare("whatever");
        long defineId = compareDefineMocker.mock(defineBuilder);
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineId, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).error(
            "BAD_SQL",
            "Elasticsearch exception [type=parsing_exception, reason=[a] query malformed, no start_object after query name]")
            .detailCount(0);
    }

//    @Test
//    public void test_error_source_bad_request_2() {
//        // mock定义
//        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
//        defineBuilder.src(TestConstants.DATASOURCE_ES, "orderNo", DEFAULT_SPLIT_MINUTES,
//                "{\"index\":\"whatever\", \"query\": {\"range\": {\"updateTime2\": {\"gte\": {begin}, \"lt\": {end}}}}}");
//        defineBuilder.target(TestConstants.DATASOURCE_ES, "orderNo", "{index: 'whatever', a: {id_s}}");
//        defineBuilder.compare("whatever");
//        long defineId = compareDefineMocker.mock(defineBuilder);
//        // mock计划
//        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineId, 0);
//        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
//                .nextTrigger(BASE_TIME).build();
//        compareScheduleMocker.mock(schedule);
//        // 执行计划
//        compareScheduleExecuteService.execute(schedule);
//        taskExecutorMocker.waitClear();
//
//        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
//                compareScheduleCheckerFactory.create(schedule.getScheduleId());
//        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
//                .errorDisabled(1).taskCount(1).currentTaskIndex(0);
//        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).error(
//                "BAD_SQL",
//                "Elasticsearch exception [type=index_not_found_exception, reason=no such index [whatever]]")
//                .detailCount(0);
//    }

}
