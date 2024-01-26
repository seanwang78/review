package com.uaepay.pub.csc.cases.facade.monitor;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.domain.enums.TaskErrorCodeEnum;
import com.uaepay.pub.csc.service.facade.MonitorTaskFacade;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.request.ManualMonitorRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.checker.monitor.MonitorTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class MonitorTaskFacadeApplyManualTest extends MockTestBase {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Autowired
    MonitorTaskFacade monitorTaskFacade;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    @Autowired
    MonitorMocker monitorMocker;

    @Autowired
    MonitorTaskCheckerFactory monitorTaskCheckerFactory;

    @Autowired
    TestDataMocker testDataMocker;

    @Test
    public void test_invalidParameter() {
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", 123);
        request.setDataBeginTime(null);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.FAIL, response.getApplyStatus());
        Assertions.assertEquals("INVALID_PARAMETER", response.getCode());
        Assertions.assertEquals("${validate.notNull}: dataBeginTime", response.getMessage());
        Assertions.assertNull(response.getResult());
    }

    @Test
    public void test_defineNotExist() {
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", 0);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.FAIL, response.getApplyStatus());
        Assertions.assertEquals("INVALID_PARAMETER", response.getCode());
        Assertions.assertEquals("define not found", response.getMessage());
        Assertions.assertNull(response.getResult());
    }

    @Test
    public void test_full() {
        taskExecutorMocker.mockFull(15000);

        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS)
            .report(TestConstants.MONITOR_REPORT_STATUS_SQL));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", defineId);

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.FAIL, response.getApplyStatus());
        Assertions.assertEquals("TASK_FULL", response.getCode());
        Assertions.assertEquals("Task full, please try again later...", response.getMessage());
        Assertions.assertNull(response.getResult());

        taskExecutorMocker.waitClear();
    }

    @Test
    public void test_mysql_invalidQueryTemplate() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder("abc").report("select abc"));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", defineId);

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().error(CommonReturnCode.INVALID_PARAMETER.getCode(), "{begin} not exist");
    }

    @Test
    public void test_mysql_badSql() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder("abc").report("{begin} {end} {count}"));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", defineId);

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().errorPrefixMessage(TaskErrorCodeEnum.BAD_SQL.getCode(),
            "StatementCallback; bad SQL grammar");
    }

    @Test
    public void test_report_mysql_noData() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS)
            .report(TestConstants.MONITOR_REPORT_STATUS_SQL));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_REPORT_STATUS).mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCount(0);
    }

    @Test
    public void test_report_mysql_hasData_noRecord() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS)
            .report(TestConstants.MONITOR_REPORT_STATUS_SQL));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_REPORT_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-01-31 23:59:59");
        testDataMocker.monitorDataBuilder("2", "S", "0.0002").updateTime("2020-02-01 02:30:00");
        testDataMocker.monitorDataBuilder("3", "S", "0.0004").updateTime("2020-02-01 03:30:00");
        testDataMocker.monitorDataBuilder("4", "F", "0.0008").updateTime("2020-02-01 04:30:00");
        testDataMocker.monitorDataBuilder("5", "F", "0.0016").updateTime("2020-02-02 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCount(1);
    }

    @Test
    public void test_report_mysql_hasData_recordDetail() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS)
            .report(TestConstants.MONITOR_REPORT_STATUS_SQL).recordDetail());
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_REPORT_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-01-31 23:59:59");
        testDataMocker.monitorDataBuilder("2", "S", "0.0002").updateTime("2020-02-01 02:30:00");
        testDataMocker.monitorDataBuilder("3", "S", "0.0004").updateTime("2020-02-01 03:30:00");
        testDataMocker.monitorDataBuilder("4", "F", "0.0008").updateTime("2020-02-01 04:30:00");
        testDataMocker.monitorDataBuilder("5", "F", "0.0016").updateTime("2020-02-02 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().recordDetailCount(1);
        taskChecker.detailChecker(0).identity("2020-02-01")
            .content("UpdateDate=2020-02-01, SuccessAmount=0.0006, SuccessCount=2, FailCount=1");
    }

    @Test
    public void test_report_mysql_hasMoreData_noRecord() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS)
            .report(TestConstants.MONITOR_REPORT_STATUS_SQL));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-03-01 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_REPORT_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.monitorDataBuilder("2", "S", "0.0002").updateTime("2020-02-02 02:30:00");
        testDataMocker.monitorDataBuilder("3", "S", "0.0004").updateTime("2020-02-03 03:30:00");
        testDataMocker.monitorDataBuilder("4", "S", "0.0008").updateTime("2020-02-04 04:30:00");
        testDataMocker.monitorDataBuilder("5", "S", "0.0016").updateTime("2020-02-05 00:00:00");
        testDataMocker.monitorDataBuilder("6", "S", "0.0032").updateTime("2020-02-06 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCountMore(5);
    }

    @Test
    public void test_report_mysql_hasMoreData_recordDetail() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS)
            .report(TestConstants.MONITOR_REPORT_STATUS_SQL).recordDetail());
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-03-01 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_REPORT_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.monitorDataBuilder("2", "S", "0.0002").updateTime("2020-02-02 02:30:00");
        testDataMocker.monitorDataBuilder("3", "S", "0.0004").updateTime("2020-02-03 03:30:00");
        testDataMocker.monitorDataBuilder("4", "S", "0.0008").updateTime("2020-02-04 04:30:00");
        testDataMocker.monitorDataBuilder("5", "S", "0.0016").updateTime("2020-02-05 00:00:00");
        testDataMocker.monitorDataBuilder("6", "S", "0.0032").updateTime("2020-02-06 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().recordDetailCountMore(5);
        taskChecker.detailChecker(0).identity("2020-02-01")
            .content("UpdateDate=2020-02-01, SuccessAmount=0.0001, SuccessCount=1, FailCount=0");
        taskChecker.detailChecker(1).identity("2020-02-02")
            .content("UpdateDate=2020-02-02, SuccessAmount=0.0002, SuccessCount=1, FailCount=0");
        taskChecker.detailChecker(2).identity("2020-02-03")
            .content("UpdateDate=2020-02-03, SuccessAmount=0.0004, SuccessCount=1, FailCount=0");
        taskChecker.detailChecker(3).identity("2020-02-04")
            .content("UpdateDate=2020-02-04, SuccessAmount=0.0008, SuccessCount=1, FailCount=0");
        taskChecker.detailChecker(4).identity("2020-02-05")
            .content("UpdateDate=2020-02-05, SuccessAmount=0.0016, SuccessCount=1, FailCount=0");
    }

    @Test
    public void test_alarm_mysql_keyFieldNotExist() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "xxx"));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-03-01 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_REPORT_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().error("SYSTEM_ERROR", "关联列不存在: xxx");
    }

    @Test
    public void test_alarm_mysql_noExpression_success() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no"));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-03-01 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCount(0);
    }

    @Test
    public void test_alarm_mysql_noExpression_fail() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no").recordDetail());
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-03-01 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail(AlarmLevelEnum.NORMAL).recordDetailCount(1);
        taskChecker.detailChecker(0).identity("1")
            .content("order_no=1, status=E, amount=0.0001, update_time=2020-02-01 00:00:00");
    }

    @Test
    public void test_alarm_mysql_expressionError() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no", "abc"));
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-03-01 00:00:00", defineId);

        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().error(TaskErrorCodeEnum.NOTIFY_EXPRESSION_ERROR.getCode(),
            "No enum constant com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum.abc");
    }

    @Test
    public void test_alarm_mysql_expression_urgent() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no", "$result.levelByCount(0,2)").recordDetail());
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-03-01 00:00:00", defineId);

        // 紧急
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2020-02-02 00:00:00");
        testDataMocker.monitorDataBuilder("3", "E", "0.0004").updateTime("2020-02-03 00:00:00");
        testDataMocker.mock();

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail(AlarmLevelEnum.URGENT).recordDetailCount(3);
        taskChecker.detailChecker(0).identity("1")
            .content("order_no=1, status=E, amount=0.0001, update_time=2020-02-01 00:00:00");
        taskChecker.detailChecker(1).identity("2")
            .content("order_no=2, status=E, amount=0.0002, update_time=2020-02-02 00:00:00");
        taskChecker.detailChecker(2).identity("3")
            .content("order_no=3, status=E, amount=0.0004, update_time=2020-02-03 00:00:00");
    }

    public static ManualMonitorRequest requestTemplate(String beginTime, String endTime, long defineId) {
        ManualMonitorRequest request = new ManualMonitorRequest();
        try {
            request.setDataBeginTime(DATE_FORMAT.parse(beginTime));
            request.setDataEndTime(DATE_FORMAT.parse(endTime));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        request.setDefineId(defineId);
        request.setOperator(CLIENT_ID);
        request.setClientId(CLIENT_ID);
        return request;
    }

}
