package com.uaepay.pub.csc.cases.domainservice.monitor.schedule;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.domain.enums.AttachType;
import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;
import com.uaepay.pub.csc.test.builder.NotifyRelationBuilder;
import com.uaepay.pub.csc.test.checker.mns.MnsApplyNotifyMocker;
import com.uaepay.pub.csc.test.domain.NotifyDefine;
import com.uaepay.pub.csc.test.mocker.NotifyRelationMocker;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import com.uaepay.pub.csc.domain.properties.ScheduleProperties;
import com.uaepay.pub.csc.domainservice.monitor.schedule.MonitorScheduleExecuteService;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.builder.MonitorScheduleBuilder;
import com.uaepay.pub.csc.test.checker.monitor.MonitorScheduleCheckerFactory;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class MonitorScheduleExecuteServiceTest extends MockTestBase {

    private static final int DEFAULT_CHECK_MINUTES = 60;
    private static final int DEFAULT_DELAY_MINUTES = 15;
    private static final String MONITOR_ALARM_STATUS_TEMP = "ut_ma_status_temp";

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

    @Autowired
    NotifyRelationMocker notifyRelationMocker;

    @Autowired
    MnsApplyNotifyMocker mnsApplyNotifyMocker;

    /**
     * 正常执行对账计划，生成2个任务，一个失败，一个成功
     */
    @Test
    public void test_normal() {
        // mock定义，计划
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no").recordDetail());
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // mock数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2020-03-01 01:30:00");
        testDataMocker.mock();

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
            .content("order_no=2, status=E, amount=0.0002, update_time=2020-03-01 01:30:00");
    }

    /**
     * 正常执行对账计划，生成2个任务，一个失败，一个成功
     */
    @Test
    public void test_report() {
        // mock定义，计划
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .report(TestConstants.MONITOR_REPORT_STATUS_SQL).recordDetail().attach(AttachType.CSV));
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        mnsApplyNotifyMocker.fixSuccess();

        NotifyRelationBuilder builder = new NotifyRelationBuilder();
        NotifyGroup group = builder.buildGroup("mail_test");
        NotifyContact contact = builder.contactBuilder("zhibin", "zhibin@abc.com").build();
        NotifyDefine define = builder.buildDefine(DefineTypeEnum.MONITOR, defineId);
        notifyRelationMocker.mock(builder.relate(group, contact).relate(define, group));

        monitorMocker.mock(schedule);

        // mock数据
        testDataMocker.reset(TestConstants.MONITOR_REPORT_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.0001").updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2020-03-01 01:30:00");
        testDataMocker.mock();

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        sleep(5000);

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME.plusHours(2)).nextTrigger(BASE_TIME.plusHours(3).plusMinutes(15))
            .currentTaskNull().version(4).taskCount(2);
    }

    /**
     * 定义不存在
     */
    @Test
    public void test_define_not_exist() {
        // mock计划
        MonitorSchedule schedule = new MonitorScheduleBuilder(0, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(BASE_TIME).currentTaskNull().version(1).errorDisabled(1)
            .taskCount(0);
    }

//    @Test
//    public void test_taskFull() {
//        // mock计划
//        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
//            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no"));
//        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
//            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
//        monitorMocker.mock(schedule);
//
//        // 模拟线程池用完
//        taskExecutorMocker.mockFull(15000);
//
//        Assertions.assertThrows(TaskFullException.class, () -> scheduleExecuteService.execute(schedule));
//
//        // 等待占坑线程结束
//        taskExecutorMocker.waitClear();
//
//        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
//            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
//        scheduleChecker.exist().checked(BASE_TIME)
//            .nextTrigger(FIXED_TIME.plusMinutes(scheduleProperties.getTaskFullDelayMinutes())).currentTaskNull()
//            .version(1).taskCount(0);
//    }

    @Test
    public void test_error_datasource_not_exist() {
        // mock定义
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no")
            .datasource(DataSourceTypeEnum.MYSQL, "whatever"));
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
            .error("CONFIG_ERROR", "datasource not exist: whatever").detailNull();
    }

    @Test
    public void test_error_query_param_not_exist() {
        // mock定义计划
        long defineId = monitorMocker
            .mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).alarm("{begin} {count}", "order_no"));
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
            .error("INVALID_PARAMETER", "{end} not exist").detailNull();
    }

    @Test
    public void test_error_bad_sql() {
        // mock定义
        long defineId = monitorMocker
            .mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP).alarm("{begin} {end} {count}", "order_no"));
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
            .errorPrefixMessage("BAD_SQL", "StatementCallback; bad SQL grammar").detailNull();
    }

    @Test
    public void test_error_notifyExpression() {
        // mock定义计划
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(MONITOR_ALARM_STATUS_TEMP)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no", "abc"));
        MonitorSchedule schedule = new MonitorScheduleBuilder(defineId, 0)
            .config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES).nextTrigger(BASE_TIME).build();
        monitorMocker.mock(schedule);

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME)
            .nextTrigger(FIXED_TIME.plusMinutes(scheduleProperties.getErrorDelayMinutes())).version(2).errorRetry(1)
            .taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .errorPrefixMessage("NOTIFY_EXPRESSION_ERROR",
                "No enum constant com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum.abc")
            .detailNull();
    }

    /**
     * 任务未正常结束，重置后重试，超过异常次数限制，计划停用
     */
    @Test
    public void test_error_retry_to_disable() {
        // mock定义计划
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no", "abc"));
        MonitorSchedule schedule =
            new MonitorScheduleBuilder(defineId, 0).config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
                .nextTrigger(BASE_TIME).currentTask(111).build();
        monitorMocker.mock(schedule);

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(FIXED_TIME.plusMinutes(DEFAULT_CHECK_MINUTES)).version(3)
            .errorDisabled(2).taskCount(1).currentTaskIndex(0);
        scheduleChecker.taskChecker(0).time(BASE_TIME, BASE_TIME.plusMinutes(DEFAULT_CHECK_MINUTES))
            .errorPrefixMessage("NOTIFY_EXPRESSION_ERROR",
                "No enum constant com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum.abc")
            .detailNull();
    }

    /**
     * 任务未正常结束，超过异常次数限制，直接停用
     */
    @Test
    public void test_error_no_retry_disable() {
        // mock定义计划
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no", "abc"));
        MonitorSchedule schedule =
            new MonitorScheduleBuilder(defineId, 0).config(BASE_TIME, DEFAULT_CHECK_MINUTES, DEFAULT_DELAY_MINUTES)
                .nextTrigger(BASE_TIME).currentTask(111).errorCount(1).build();
        monitorMocker.mock(schedule);

        // 执行计划
        scheduleExecuteService.execute(schedule);
        taskExecutorMocker.waitClear();

        // 检查计划及任务
        MonitorScheduleCheckerFactory.ScheduleChecker scheduleChecker =
            monitorScheduleCheckerFactory.create(schedule.getScheduleId());
        scheduleChecker.exist().checked(BASE_TIME).nextTrigger(BASE_TIME).version(1).errorDisabled(2).taskCount(0)
            .currentTask(111);
    }

}
