package com.uaepay.pub.csc.cases.facade.compare;

import java.text.ParseException;

import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.pub.csc.service.facade.CompareTaskFacade;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;
import com.uaepay.pub.csc.service.facade.request.RetryCompareRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.CompareDefineMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class CompareTaskFacadeApplyRetryTest extends MockTestBase {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final String DATE_SET = "unittest-ApplyRetryFacadeTest";

    @Autowired
    CompareDefineMocker compareDefineMocker;

    @Autowired
    TestDataMocker testDataMocker;

    @Autowired
    CompareTaskFacade compareTaskFacade;

    @Autowired
    CompareTaskCheckerFactory compareTaskCheckerFactory;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    private long defineId;

    @BeforeAll
    public void setUp() {
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(DATE_SET);
        defineBuilder.src("order_no", 1440,
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '" + DATE_SET
                + "' and data_type = 'S'"
                + " and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}");
        defineBuilder.target("order_no",
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '" + DATE_SET
                + "' and data_type = 'T'" + " and order_no in {id_s}");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "  $data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n"
            + "  $data.isEqual('amount', 'amount')");
        defineId = compareDefineMocker.mock(defineBuilder);
    }

    /**
     * 对账失败，重试成功
     */
    @Test
    public void test_fail_retry_success() throws InterruptedException, ParseException {
        Long origTaskId = mockFailTask();

        mockSuccessRetry();

        // 第一次重试
        RetryCompareRequest retryRequest = retryCompareTemplate(origTaskId);
        ObjectQueryResponse<Long> retryResponse = compareTaskFacade.applyRetryCompare(retryRequest);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, retryResponse.getApplyStatus());
        taskExecutorMocker.waitClear();
        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(retryResponse.getResult());
        taskChecker.exist().success("pass: 1").detailCount(0);
        taskChecker = compareTaskCheckerFactory.create(origTaskId);
        taskChecker.exist().retrySuccess(retryResponse.getResult()).detailCount(1);

        // 第二次重试
        RetryCompareRequest retryRequest2 = retryCompareTemplate(origTaskId);
        ObjectQueryResponse<Long> retryResponse2 = compareTaskFacade.applyRetryCompare(retryRequest2);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, retryResponse2.getApplyStatus());
        taskExecutorMocker.waitClear();
        CompareTaskCheckerFactory.TaskChecker taskChecker2 = compareTaskCheckerFactory.create(retryResponse2.getResult());
        taskChecker2.exist().success("pass: 1").detailCount(0);
        taskChecker2 = compareTaskCheckerFactory.create(origTaskId);
        // 重试任务号还是第一次重试的
        taskChecker2.exist().retrySuccess(retryResponse.getResult()).detailCount(1);
    }

    /**
     * 对账成功，重试成功，原任务状态不改变
     */
    @Test
    public void test_success_retry_success() throws InterruptedException, ParseException {
        Long origTaskId = mockSuccessTask();
        mockSuccessRetry();
        RetryCompareRequest retryRequest = retryCompareTemplate(origTaskId);
        ObjectQueryResponse<Long> retryResponse = compareTaskFacade.applyRetryCompare(retryRequest);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, retryResponse.getApplyStatus());


        taskExecutorMocker.waitClear();
        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(retryResponse.getResult());
        taskChecker.exist().success("pass: 1").detailCount(0);
        taskChecker = compareTaskCheckerFactory.create(origTaskId);
        taskChecker.exist().success("pass: 1").detailCount(0);
    }

    /**
     * mock 一个对账失败的任务
     */
    private Long mockFailTask() throws ParseException, InterruptedException {
        // 模拟一个失败的对账
        testDataMocker.reset(DATE_SET);
        testDataMocker.srcDataBuilder("1", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.targetDataBuilder("1", "Fail", "0.0001").updateTime("2019-10-01 00:00:00");
        testDataMocker.mock();
        ManualCompareRequest request = manualCompareTemplate("2019-10-01 01:00:00", "2019-10-02 00:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        taskExecutorMocker.waitClear();
        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail("pass: 0, mismatch: 1").detailCount(1);
        return response.getResult();
    }

    /**
     * mock 一个对账成功的任务
     */
    private Long mockSuccessTask() throws ParseException, InterruptedException {
        // 模拟一个失败的对账
        testDataMocker.reset(DATE_SET);
        testDataMocker.srcDataBuilder("1", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.targetDataBuilder("1", "Success", "0.0001").updateTime("2019-10-01 00:00:00");
        testDataMocker.mock();
        ManualCompareRequest request = manualCompareTemplate("2019-10-01 01:00:00", "2019-10-02 00:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        taskExecutorMocker.waitClear();
        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success("pass: 1").detailCount(0);
        return response.getResult();
    }

    private void mockSuccessRetry() {
        testDataMocker.reset(DATE_SET);
        testDataMocker.srcDataBuilder("1", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.targetDataBuilder("1", "Success", "0.0001").updateTime("2019-10-01 00:00:00");
        testDataMocker.mock();
    }

    public ManualCompareRequest manualCompareTemplate(String beginTime, String endTime, long defineId)
        throws ParseException {
        ManualCompareRequest request = new ManualCompareRequest();
        request.setDataBeginTime(DATE_FORMAT.parse(beginTime));
        request.setDataEndTime(DATE_FORMAT.parse(endTime));
        request.setDefineId(defineId);
        request.setOperator(CLIENT_ID);
        request.setClientId(CLIENT_ID);
        return request;
    }

    public RetryCompareRequest retryCompareTemplate(long origTaskId) {
        RetryCompareRequest request = new RetryCompareRequest();
        request.setTaskId(origTaskId);
        request.setOperator(CLIENT_ID);
        request.setClientId(CLIENT_ID);
        return request;
    }

}
