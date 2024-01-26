package com.uaepay.pub.csc.cases.domainservice.compare.schedule;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.domain.properties.ScheduleProperties;
import com.uaepay.pub.csc.domainservice.compare.schedule.CompareScheduleExecuteService;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.builder.CompareScheduleBuilder;
import com.uaepay.pub.csc.test.checker.compare.CompareScheduleCheckerFactory;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.CompareDefineMocker;
import com.uaepay.pub.csc.test.mocker.CompareScheduleMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

@Disabled
public class CompareScheduleExecuteServiceTest extends MockTestBase {

    private static final String SCHEDULE_NORMAL = "unittest-schedule_normal";
    private static final String SCHEDULE_DS_ERROR = "unittest-schedule_ds_error";
    private static final String SCHEDULE_ERROR = "unittest-schedule_error";
    private static final String SCHEDULE_RECOVERABLE_ERROR = "unittest-schedule_rc_error";

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

    private long defineRecoverableError;

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
        defineBuilder = new CompareDefineBuilder(SCHEDULE_NORMAL);
        defineBuilder.src("order_no", DEFAULT_SPLIT_MINUTES,
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '"
                + SCHEDULE_NORMAL + "' and data_type = 'S'"
                + " and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}");
        defineBuilder.target("order_no",
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '"
                + SCHEDULE_NORMAL + "' and data_type = 'T'" + " and order_no in {id_s}");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "$data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n" + " $data.isEqual('amount', 'amount')");
        defineNormal = compareDefineMocker.mock(defineBuilder);
        // 异常定义
        defineBuilder = new CompareDefineBuilder(SCHEDULE_RECOVERABLE_ERROR);
        defineBuilder.src("order_no", DEFAULT_SPLIT_MINUTES,
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '"
                + SCHEDULE_RECOVERABLE_ERROR + "' and data_type = 'S'"
                + " and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}");
        defineBuilder.target("order_no",
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '"
                + SCHEDULE_RECOVERABLE_ERROR + "' and data_type = 'T'" + " and order_no in {id_s}");
        defineBuilder.compare("$data.isCorrespond('xxx')");
        defineRecoverableError = compareDefineMocker.mock(defineBuilder);
        // mock数据
        testDataMocker.reset(SCHEDULE_RECOVERABLE_ERROR);
        testDataMocker.srcDataBuilder("a", "S", "0.01").updateTime(BASE_TIME.plusMinutes(10));
        testDataMocker.targetDataBuilder("a", "Success", "0.01");
        testDataMocker.mock();
    }

    @Test
    public void test_normal() {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineNormal, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);
        // mock数据
        testDataMocker.reset(SCHEDULE_NORMAL);
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime(BASE_TIME.plusMinutes(10));
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.1234")
            .updateTime(BASE_TIME.plusHours(1).plusMinutes(20));
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime(BASE_TIME.plusHours(1).plusMinutes(30));
        testDataMocker.targetDataBuilder("a", "Success", "0.0001");
        testDataMocker.targetDataBuilder("b", "Fail", "123456789012345.1234");
        testDataMocker.targetDataBuilder("c", "Failure", "0.01");
        testDataMocker.mock();
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();
        // waitFinish(20000);

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
        testDataMocker.reset(SCHEDULE_NORMAL);
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime(BASE_TIME.plusMinutes(10));
        testDataMocker.srcDataBuilder("b", "F", "0.02").updateTime(BASE_TIME.plusHours(1).plusMinutes(20));
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime(BASE_TIME.plusHours(1).plusMinutes(30));
        testDataMocker.targetDataBuilder("a", "Success", "0.0001").updateTime(BASE_TIME);
        testDataMocker.targetDataBuilder("b", "Process", "0.03").updateTime(BASE_TIME);
        testDataMocker.mock();
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();
        // waitFinish(25000);

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
            .lackTarget("order_no=c, status=F, amount=0.0100, update_time=2019-12-20 01:30:00.0");
        taskChecker2.detailChecker(1).identity("b").mismatch(
            "order_no=b, status=F, amount=0.0200, update_time=2019-12-20 01:20:00.0",
            "order_no=b, status=Process, amount=0.0300, update_time=2019-12-20 00:00:00.0",
            "status[F] and status[Process] not correspond, rule: [F] -> [Fail,Failure], amount[0.0200] and amount[0.0300] not equal");
    }

    @Test
    public void test_error_define_not_found() {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(0, 0);
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
            .error("CONFIG_ERROR", "define not found").detailCount(0);
    }

    @Test
    public void test_error_datasource_not_exist() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_DS_ERROR);
        defineBuilder.src("whatever", DEFAULT_SPLIT_MINUTES, "whatever");
        defineBuilder.srcDatasourceCode("whatever");
        defineBuilder.target("order_no", "whatever");
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
        // waitFinish(5000);

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(1).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("CONFIG_ERROR", "datasource not exist: whatever").detailCount(0);
    }

    @Test
    public void test_error_source_sql_param_not_exist() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src("order_no", DEFAULT_SPLIT_MINUTES, "{begin} {offset} {count}");
        defineBuilder.target("order_no", "whatever");
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
            .error("INVALID_PARAMETER", "{end} not exist").detailCount(0);
    }

    @Test
    public void test_error_target_sql_param_not_exist() {
        // mock定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(SCHEDULE_ERROR);
        defineBuilder.src("order_no", DEFAULT_SPLIT_MINUTES, "{begin} {end} {offset} {count}");
        defineBuilder.target("order_no", "whatever");
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
        defineBuilder.src("order_no", DEFAULT_SPLIT_MINUTES, "{begin} {end} {offset} {count}");
        defineBuilder.target("order_no", "{id_s}");
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
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).error("BAD_SQL",
            "StatementCallback; bad SQL grammar [str_to_date('2019-12-20 00:00:00', '%Y-%m-%d %H:%i:%s') str_to_date('2019-12-20 00:30:00', '%Y-%m-%d %H:%i:%s') 0 2];"
                + " nested exception is com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: You have an er...")
            .detailCount(0);
    }

    @Test
    public void test_error_recoverable() {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineRecoverableError, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);

        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME)
            .nextTrigger(FIXED_TIME.plusMinutes(scheduleProperties.getErrorDelayMinutes())).version(2).errorRetry(1)
            .taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("CHECK_EXPRESSION_ERROR", "$data.isCorrespond('xxx')").detailCount(0);
    }

    @Test
    public void test_error_to_disable() {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineRecoverableError, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).errorCount(1).build();
        compareScheduleMocker.mock(schedule);

        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(2)
            .errorDisabled(2).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("CHECK_EXPRESSION_ERROR", "$data.isCorrespond('xxx')").detailCount(0);
    }

    /**
     * 计划任务未正常结束，重置后重试，超过异常次数限制，计划停用
     */
    @Test
    public void test_error_retry_to_disable() {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineRecoverableError, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).currentTask(404).build();
        compareScheduleMocker.mock(schedule);

        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(3)
            .errorDisabled(2).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .error("CHECK_EXPRESSION_ERROR", "$data.isCorrespond('xxx')").detailCount(0);
    }

    /**
     * 计划任务未正常结束，超过异常次数限制，直接停用
     */
    @Test
    public void test_error_no_retry_disable() {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineRecoverableError, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).currentTask(404).errorCount(1).build();
        compareScheduleMocker.mock(schedule);
        // 执行计划
        compareScheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();
        // waitFinish(5000);

        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(BASE_TIME).version(1).errorDisabled(2).taskCount(0)
            .currentTask(404);
    }

    @Test
    public void test_taskFull() {
        // mock计划
        CompareScheduleBuilder scheduleBuilder = new CompareScheduleBuilder(defineNormal, 0);
        CompareSchedule schedule = scheduleBuilder.config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
            .nextTrigger(BASE_TIME).build();
        compareScheduleMocker.mock(schedule);

        // 占满线程
        taskExecutorMocker.mockFull(15000);

        // 执行计划
        Assertions.assertThrows(TaskFullException.class, () -> compareScheduleExecuteService.execute(schedule));

        // 校验
        CompareScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            compareScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME)
            .nextTrigger(FIXED_TIME.plusMinutes(scheduleProperties.getTaskFullDelayMinutes())).version(1)
            .currentTaskNull().taskCount(0);

        // 等待占坑线程结束
        taskExecutorMocker.waitClear();
    }

    /**
     * 等待有空闲线程
     */
    public void waitFinish(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("等待结束: " + DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()));
    }

}
