package com.uaepay.pub.csc.cases.facade.monitor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.pub.csc.service.facade.MonitorTaskFacade;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.request.ManualMonitorRequest;
import com.uaepay.pub.csc.service.facade.request.RetryMonitorRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.checker.monitor.MonitorTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class MonitorTaskFacadeApplyRetryTest extends MockTestBase {

    @Autowired
    MonitorMocker monitorMocker;

    @Autowired
    TestDataMocker testDataMocker;

    @Autowired
    MonitorTaskFacade monitorTaskFacade;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    @Autowired
    MonitorTaskCheckerFactory monitorTaskCheckerFactory;

    long defineAlarmStatus;

    @BeforeAll
    public void setUp() {
        defineAlarmStatus = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no").recordDetail());
    }

    /**
     * 任务失败，重试成功，原任务更新状态到重试成功
     */
    @Test
    public void test_fail_retry_success() {
        // 准备一个失败的任务
        Long origTaskId = mockFailTask();

        // mock重试成功数据
        mockSuccessRetry();

        // 请求
        RetryMonitorRequest request = retryTemplate(origTaskId);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyRetry(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());

        // 等待结束
        taskExecutorMocker.waitClear();

        // 校验
        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCount(0);
        taskChecker = monitorTaskCheckerFactory.create(origTaskId);
        taskChecker.exist().retrySuccess(response.getResult()).recordDetailCount(1);
    }

    /**
     * 任务失败，重试失败，原任务状态不变
     */
    @Test
    public void test_fail_retry_fail() {
        // 准备一个失败的任务
        Long origTaskId = mockFailTask();

        // mock重试失败数据
        mockFailRetry();

        // 请求
        RetryMonitorRequest request = retryTemplate(origTaskId);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyRetry(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());

        // 等待结束
        taskExecutorMocker.waitClear();

        // 校验
        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail(AlarmLevelEnum.NORMAL).recordDetailCount(1);
        taskChecker = monitorTaskCheckerFactory.create(origTaskId);
        taskChecker.exist().fail(AlarmLevelEnum.NORMAL).recordDetailCount(1);
    }

    /**
     * 任务成功，重试成功，原任务状态不变
     */
    @Test
    public void test_success_retry_success() {
        // 准备一个成功的任务
        Long origTaskId = mockSuccessTask();

        // mock重试失败数据
        mockSuccessRetry();

        // 请求
        RetryMonitorRequest request = retryTemplate(origTaskId);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyRetry(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());

        // 等待结束
        taskExecutorMocker.waitClear();

        // 校验
        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCount(0);
        taskChecker = monitorTaskCheckerFactory.create(origTaskId);
        taskChecker.exist().success().detailCount(0);
    }

    /**
     * 任务成功，重试失败，原任务状态不变
     */
    @Test
    public void test_success_retry_fail() {
        // 准备一个成功的任务
        Long origTaskId = mockSuccessTask();

        // mock重试失败数据
        mockFailRetry();

        // 请求
        RetryMonitorRequest request = retryTemplate(origTaskId);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyRetry(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());

        // 等待结束
        taskExecutorMocker.waitClear();

        // 校验
        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail(AlarmLevelEnum.NORMAL).recordDetailCount(1);
        taskChecker = monitorTaskCheckerFactory.create(origTaskId);
        taskChecker.exist().success().detailCount(0);
    }

    /**
     * mock 一个失败的任务
     */
    private Long mockFailTask() {
        // 模拟一个失败的报警
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.mock();

        ManualMonitorRequest request = MonitorTaskFacadeApplyManualTest.requestTemplate("2020-02-01 00:00:00",
            "2020-03-01 00:00:00", defineAlarmStatus);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail(AlarmLevelEnum.NORMAL).recordDetailCount(1);
        taskChecker.detailChecker(0).identity("1")
            .content("order_no=1, status=E, amount=0.0001, update_time=2020-02-01 00:00:00");
        return response.getResult();
    }

    /**
     * mock 一个成功的任务
     */
    private Long mockSuccessTask() {
        // 模拟一个成功的报警
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.mock();

        ManualMonitorRequest request = MonitorTaskFacadeApplyManualTest.requestTemplate("2020-02-01 00:00:00",
            "2020-03-01 00:00:00", defineAlarmStatus);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCount(0);
        return response.getResult();
    }

    private void mockSuccessRetry() {
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.srcDataBuilder("1", "S", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.mock();
    }

    private void mockFailRetry() {
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.srcDataBuilder("1", "E", "0.0001").updateTime("2020-02-01 00:00:00");
        testDataMocker.mock();
    }

    public RetryMonitorRequest retryTemplate(long origTaskId) {
        RetryMonitorRequest request = new RetryMonitorRequest();
        request.setTaskId(origTaskId);
        request.setOperator(CLIENT_ID);
        request.setClientId(CLIENT_ID);
        return request;
    }

}
