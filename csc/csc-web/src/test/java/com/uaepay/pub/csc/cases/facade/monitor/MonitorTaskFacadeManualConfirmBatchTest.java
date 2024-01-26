package com.uaepay.pub.csc.cases.facade.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
import com.uaepay.pub.csc.service.facade.request.ManualConfirmBatchRequest;
import com.uaepay.pub.csc.service.facade.request.ManualMonitorRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.checker.monitor.MonitorTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;
import com.uaepay.unittest.facade.mocker.testtool.checker.CommonResponseChecker;

public class MonitorTaskFacadeManualConfirmBatchTest extends MockTestBase {

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
    ManualConfirmBatchRequest request;
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
        request.setTaskIds(null);
        invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "must not be null: taskIds");

        request = templateRequest();
        request.setTaskIds(new ArrayList<>());
        invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "size must be between 1 and 200: taskIds");

        request = templateRequest();
        request.setTaskIds(Collections.nCopies(201, 0L));
        invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "size must be between 1 and 200: taskIds");

        request = templateRequest();
        request.setErrorMessage(null);
        invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "must not be blank: errorMessage");
    }

    @Test
    public void testSuccess() {
        // 准备一个失败的任务
        Long taskId1 = mockFailTask();
        Long taskId2 = mockFailTask();

        // 执行 & 校验
        request = templateRequest();
        request.setTaskIds(Arrays.asList(taskId1, taskId2));
        request.setErrorMessage(RandomStringUtils.randomAlphabetic(6));
        request.setOperator(OPERATOR);
        invoke(request).success(null, "Update success count: 2");
        monitorTaskCheckerFactory.create(taskId1).manualConfirmed(request.getErrorMessage(), OPERATOR);
        monitorTaskCheckerFactory.create(taskId2).manualConfirmed(request.getErrorMessage(), OPERATOR);
    }

    @Test
    public void testComplex() {
        // 准备一个失败的任务
        Long successId = mockSuccessTask();
        Long failId = mockFailTask();
        long notExistId = 0L;

        // 执行 & 校验
        request = templateRequest();
        request.setTaskIds(Arrays.asList(failId, successId, notExistId));
        request.setErrorMessage(RandomStringUtils.randomAlphabetic(6));
        request.setOperator(OPERATOR);
        invoke(request).success(null, "Update success count: 1, update fail count: 2");

        monitorTaskCheckerFactory.create(successId).success();
        monitorTaskCheckerFactory.create(failId).manualConfirmed(request.getErrorMessage(), OPERATOR);
        monitorTaskCheckerFactory.create(notExistId).notExist();
    }

    private ManualConfirmBatchRequest templateRequest() {
        ManualConfirmBatchRequest result = new ManualConfirmBatchRequest();
        result.setTaskIds(Collections.singletonList(0L));
        result.setErrorMessage(WHATEVER);
        result.setOperator(OPERATOR);
        result.setClientId(CLIENT_ID);
        return result;
    }

    private CommonResponseChecker invoke(ManualConfirmBatchRequest request) {
        CommonResponse response = monitorTaskFacade.manualConfirmBatch(request);
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
