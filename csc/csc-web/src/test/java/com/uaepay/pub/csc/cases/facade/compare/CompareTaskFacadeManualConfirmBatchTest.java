package com.uaepay.pub.csc.cases.facade.compare;

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
import com.uaepay.pub.csc.service.facade.CompareTaskFacade;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmBatchRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.CompareDefineMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;
import com.uaepay.unittest.facade.mocker.testtool.checker.CommonResponseChecker;

public class CompareTaskFacadeManualConfirmBatchTest extends MockTestBase {

    private static final String DATA_SET = "unittest-CManualConfirmTest";

    @Autowired
    CompareDefineMocker compareDefineMocker;

    @Autowired
    CompareTaskFacade compareTaskFacade;

    @Autowired
    TestDataMocker testDataMocker;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    @Autowired
    CompareTaskCheckerFactory compareTaskCheckerFactory;

    long defineId;
    ManualConfirmBatchRequest request;
    CompareTaskCheckerFactory.TaskChecker taskChecker;

    @BeforeAll
    public void setUp() {
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(DATA_SET);
        defineBuilder.src("order_no", 1440,
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '" + DATA_SET
                + "' and data_type = 'S'"
                + " and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}");
        defineBuilder.target("order_no",
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '" + DATA_SET
                + "' and data_type = 'T'" + " and order_no in {id_s}");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "  $data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n"
            + "  $data.isEqual('amount', 'amount')");
        defineId = compareDefineMocker.mock(defineBuilder);
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
        compareTaskCheckerFactory.create(taskId1).manualConfirmed(request.getErrorMessage(), OPERATOR);
        compareTaskCheckerFactory.create(taskId2).manualConfirmed(request.getErrorMessage(), OPERATOR);
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

        compareTaskCheckerFactory.create(successId).success();
        compareTaskCheckerFactory.create(failId).manualConfirmed(request.getErrorMessage(), OPERATOR);
        compareTaskCheckerFactory.create(notExistId).notExist();
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
        CommonResponse response = compareTaskFacade.manualConfirmBatch(request);
        return new CommonResponseChecker(response);
    }

    /**
     * mock 一个成功的任务
     */
    private Long mockSuccessTask() {
        // 模拟一个成功的数据
        testDataMocker.reset(DATA_SET);
        testDataMocker.mock();

        ManualCompareRequest request =
            CompareTaskFacadeApplyManualTest.requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success("pass: 0").detailCount(0);
        return response.getResult();
    }

    /**
     * mock 一个失败的任务
     */
    private Long mockFailTask() {
        // 模拟一个失败的报警
        testDataMocker.reset(DATA_SET);
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime("2021-12-15 01:00:00");
        testDataMocker.mock();

        ManualCompareRequest request =
            CompareTaskFacadeApplyManualTest.requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail("pass: 0, lack: 1").detailCount(1);
        taskChecker.detailChecker(0).identity("a")
            .lackTarget("order_no=a, status=S, amount=0.0001, update_time=2021-12-15T01:00:00+04");
        return response.getResult();
    }

}
