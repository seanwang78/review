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

public class CompareScheduleExecuteMongoTest extends MockTestBase {

    private static final String SCHEDULE_NORMAL_FIND = "unittest-mongo_find";
    private static final String SCHEDULE_ERROR = "unittest-mongo_error";

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
        BASE_TIME = new DateTime(2019, 12, 20, 0, 0, 0);
        FIXED_TIME = BASE_TIME.plusHours(3);
        DateTimeUtils.setCurrentMillisFixed(FIXED_TIME.getMillis());
    }

    @BeforeAll
    public void setUp() {
        // mock公共对账定义
        CompareDefineBuilder defineBuilder;
        // 正常定义
        defineBuilder = new CompareDefineBuilder(SCHEDULE_NORMAL_FIND);
        defineBuilder.src(TestConstants.DATASOURCE_MONGO, "orderNo", DEFAULT_SPLIT_MINUTES,
            "{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: '" + SCHEDULE_NORMAL_FIND
                + "'}, {dataType: 'S'}" + ", {updateTime: {$gte: {begin}, $lt: {end}}}"
                + "]}, projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1}, sort: {orderNo: 1}}");
        defineBuilder.target(TestConstants.DATASOURCE_MONGO, "orderNo",
            "{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: '" + SCHEDULE_NORMAL_FIND
                + "'}, {dataType: 'T'}, {orderNo:{$in:{id_s}}}]}"
                + ", projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1}}");
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
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.1234")
            .updateTime(BASE_TIME.plusHours(1).plusMinutes(20));
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime(BASE_TIME.plusHours(1).plusMinutes(30));
        testDataMocker.targetDataBuilder("a", "Success", "0.0001");
        testDataMocker.targetDataBuilder("b", "Fail", "123456789012345.1234");
        testDataMocker.targetDataBuilder("c", "Failure", "0.01");
        testDataMocker.mockMongo();
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
    public void test_fail() {
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
        testDataMocker.mockMongo();
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

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
        taskChecker2.detailChecker(0).identity("c")
            .lackTarget("orderNo=c, status=F, amount=0.01, updateTime=2019-12-20T01:30:00+04");
        taskChecker2.detailChecker(1).identity("b").mismatch(
            "orderNo=b, status=F, amount=0.02, updateTime=2019-12-20T01:20:00+04",
            "orderNo=b, status=Process, amount=0.03, updateTime=2019-12-20T00:00:00+04",
            "status[F] and status[Process] not correspond, rule: [F] -> [Fail,Failure], amount[0.02] and amount[0.03] not equal");
    }

    /**
     * 查询指令无法解析
     */
    @Test
    public void test_error_query_command() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_MONGO, "orderNo", DEFAULT_SPLIT_MINUTES, "whatever");
        defineBuilder.target(TestConstants.DATASOURCE_MONGO, "orderNo", "whatever");
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
    public void test_error_source_sql_db_not_exist() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_MONGO, "orderNo", DEFAULT_SPLIT_MINUTES, "{}");
        defineBuilder.target(TestConstants.DATASOURCE_MONGO, "orderNo", "whatever");
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
            .error("CONFIG_ERROR", "source sql 'db' not exist").detailCount(0);
    }

    @Test
    public void test_error_source_sql_param_not_exist() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_MONGO, "orderNo", DEFAULT_SPLIT_MINUTES, "{db:'whatever'}");
        defineBuilder.target(TestConstants.DATASOURCE_MONGO, "orderNo", "whatever");
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
        defineBuilder.src(TestConstants.DATASOURCE_MONGO, "orderNo", DEFAULT_SPLIT_MINUTES,
            "{db:'whatever', a: {begin}, b: {end}}");
        defineBuilder.target(TestConstants.DATASOURCE_MONGO, "orderNo", "{}");
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

    @Test
    public void test_error_source_bad_sql() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src(TestConstants.DATASOURCE_MONGO, "orderNo", DEFAULT_SPLIT_MINUTES,
            "{db:'testdb', find: 'csc_test_data', a: {begin}, b: {end}}");
        defineBuilder.target(TestConstants.DATASOURCE_MONGO, "orderNo", "{db: 'whatever', a: {id_s}}");
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
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).errorPrefix(
            "BAD_SQL",
            "Command failed with error 9 (FailedToParse): 'Failed to parse: { find: \"csc_test_data\", a: new Date(1576785600000), b: new Date(1576787400000), skip: 0, batchSize: 2")
            .detailCount(0);
    }

}
