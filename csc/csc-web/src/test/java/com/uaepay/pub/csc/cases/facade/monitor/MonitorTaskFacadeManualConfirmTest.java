package com.uaepay.pub.csc.cases.facade.monitor;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.service.facade.MonitorTaskFacade;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmMonitorRequest;
import com.uaepay.pub.csc.service.facade.request.ManualMonitorRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.checker.monitor.MonitorTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;
import com.uaepay.unittest.facade.mocker.testtool.checker.CommonResponseChecker;

public class MonitorTaskFacadeManualConfirmTest extends MockTestBase {

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
    ManualConfirmMonitorRequest request;
    MonitorTaskCheckerFactory.TaskChecker taskChecker;

    @BeforeAll
    public void setUp() {
        defineAlarmStatus = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_ALARM_STATUS)
            .alarm(TestConstants.MONITOR_ALARM_STATUS_SQL, "order_no").recordDetail());
    }

    @Test
    public void testValidateParameter() {
        request = templateRequest();
        request.setOperator(null);
        invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "${validate.notBlank}: operator");

        request = templateRequest();
        request.setErrorMessage(null);
        invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "${validate.notBlank}: errorMessage");
    }

    @Test
    public void testTaskNotExist() {
        request = templateRequest();
        request.setTaskId(0L);
        invoke(request).fail(CommonReturnCode.BIZ_CHECK_FAIL, "task not exist");
    }

    @Test
    public void testTaskStatusError() {
        // 准备一个成功的任务
        Long taskId = mockSuccessTask();

        request = templateRequest();
        request.setTaskId(taskId);
        invoke(request).fail(CommonReturnCode.BIZ_CHECK_FAIL, "task status error");
    }

    @Test
    public void testSuccess() {
        // 准备一个失败的任务
        Long taskId = mockFailTask();

        // 执行 & 校验
        request = templateRequest();
        request.setTaskId(taskId);
        request.setErrorMessage(RandomStringUtils.randomAlphabetic(6));
        request.setOperator(OPERATOR);
        invoke(request).success(null, null);
        taskChecker = monitorTaskCheckerFactory.create(taskId);
        taskChecker.exist().manualConfirmed(request.getErrorMessage(), OPERATOR);

        // 重复请求
        invoke(request).success(null, "Duplicate request.");
    }

    private ManualConfirmMonitorRequest templateRequest() {
        ManualConfirmMonitorRequest result = new ManualConfirmMonitorRequest();
        result.setTaskId(0L);
        result.setErrorMessage(WHATEVER);
        result.setOperator(OPERATOR);
        result.setClientId(CLIENT_ID);
        return result;
    }

    private CommonResponseChecker invoke(ManualConfirmMonitorRequest request) {
        CommonResponse response = monitorTaskFacade.manualConfirm(request);
        return new CommonResponseChecker(response);
    }

    /**
     * mock 一个成功的任务
     */
    private Long mockSuccessTask() {
        // 模拟一个成功的报警
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2021-07-01 00:00:00");
        testDataMocker.mock();

        ManualMonitorRequest request = MonitorTaskFacadeApplyManualTest.requestTemplate("2021-07-01 00:00:00",
            "2021-07-10 00:00:00", defineAlarmStatus);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCount(0);
        return response.getResult();
    }

    /**
     * mock 一个失败的任务
     */
    private Long mockFailTask() {
        // 模拟一个失败的报警
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.0001").updateTime("2021-07-01 00:00:00");
        testDataMocker.mock();

        ManualMonitorRequest request = MonitorTaskFacadeApplyManualTest.requestTemplate("2021-07-01 00:00:00",
            "2021-07-10 00:00:00", defineAlarmStatus);
        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail(AlarmLevelEnum.NORMAL).recordDetailCount(1);
        taskChecker.detailChecker(0).identity("1")
            .content("order_no=1, status=E, amount=0.0001, update_time=2021-07-01 00:00:00");
        return response.getResult();
    }

}
