package com.uaepay.pub.csc.cases.facade.compare;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.basis.compensation.event.domainservice.CompensationEventRetryService;
import com.uaepay.pub.csc.service.facade.CompareTaskFacade;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.CompareDefineMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class CompareTaskFacadeApplyManualTest extends MockTestBase {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final String DATA_SET = "unittest-ApplyManualFacadeTest";
    private static final String DATA_SET_COMPENSATE = "unittest-ApplyManual-withCP";
    private static final String DATA_SET_EMPTY = "unittest-ApplyManualFacadeTest-empty";

    @Autowired
    CompareDefineMocker compareDefineMocker;

    @Autowired
    TestDataMocker testDataMocker;

    @Autowired
    CompareTaskFacade compareTaskFacade;

    @Autowired
    CompareTaskCheckerFactory compareTaskCheckerFactory;

    @Autowired
    CompensationEventRetryService compensationEventRetryService;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    private long defineId, defineIdCompensate;

    @BeforeAll
    public void setUp() {
        // 无补单对账定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(DATA_SET);
        defineBuilder.src("order_no", 60,
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

        // 补单对账定义
        defineBuilder = new CompareDefineBuilder(DATA_SET_COMPENSATE);
        defineBuilder.src("order_no", 1440,
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '"
                + DATA_SET_COMPENSATE + "' and data_type = 'S'"
                + " and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}");
        defineBuilder.target("order_no",
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '"
                + DATA_SET_COMPENSATE + "' and data_type = 'T'" + " and order_no in {id_s}");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "  $data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n"
            + "  $data.isEqual('amount', 'amount')");
        defineBuilder.compensate(
            "$result.lack('status', 'P')\n$result.mismatch('status', 'status', 'S', 'Process', 'S1')",
            "{'appCode': 'gp056_csc', notifyType:'test'}");
        defineIdCompensate = compareDefineMocker.mock(defineBuilder);
    }

    @Test
    public void test_success() {
        testDataMocker.reset(DATA_SET);
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.1234").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.targetDataBuilder("a", "Success", "0.0001");
        testDataMocker.targetDataBuilder("b", "Fail", "123456789012345.1234");
        testDataMocker.targetDataBuilder("c", "Failure", "0.01");
        testDataMocker.mock();

        ManualCompareRequest request = requestTemplate("2019-10-01 01:00:00", "2019-10-01 04:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success("pass: 3").detailCount(0);
    }

    @Test
    public void test_fail() {
        testDataMocker.reset(DATA_SET);
        testDataMocker.srcDataBuilder("1", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("2", "F", "0.0002").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("3", "F", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.srcDataBuilder("4", "S", "0.01").updateTime("2019-10-01 03:10:00");
        testDataMocker.targetDataBuilder("1", "Success", "0.0001").updateTime("2019-10-01 00:00:00");
        testDataMocker.targetDataBuilder("2", "Success", "0.0002").updateTime("2019-10-01 00:00:00");
        testDataMocker.targetDataBuilder("3", "Success", "0.02").updateTime("2019-10-01 00:00:00");
        testDataMocker.mock();

        ManualCompareRequest request = requestTemplate("2019-10-01 01:00:00", "2019-10-01 04:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail("pass: 1, lack: 1, mismatch: 2").detailCount(3);
        taskChecker.detailChecker(0).identity("4")
            .lackTarget("order_no=4, status=S, amount=0.0100, update_time=2019-10-01T03:10:00+04");
        taskChecker.detailChecker(1).identity("2").mismatch(
            "order_no=2, status=F, amount=0.0002, update_time=2019-10-01T02:30:00+04",
            "order_no=2, status=Success, amount=0.0002, update_time=2019-10-01T00:00:00+04",
            "status[F] and status[Success] not correspond, rule: [F] -> [Fail,Failure]");
        taskChecker.detailChecker(2).identity("3").mismatch(
            "order_no=3, status=F, amount=0.0100, update_time=2019-10-01T03:00:00+04",
            "order_no=3, status=Success, amount=0.0200, update_time=2019-10-01T00:00:00+04",
            "status[F] and status[Success] not correspond, rule: [F] -> [Fail,Failure], amount[0.01] and amount[0.02] not equal");
    }

    /**
     * 对账不一致，补单成功，重新对账成功
     */
    @Test
    public void test_compensate() throws InterruptedException {
        testDataMocker.reset(DATA_SET_COMPENSATE);
        // 核对一致
        testDataMocker.srcDataBuilder("1", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.targetDataBuilder("1", "Success", "0.0001").updateTime("2019-10-01 01:00:00");
        // 核对不一致，不补单
        testDataMocker.srcDataBuilder("2", "F", "0.0002").updateTime("2019-10-01 02:00:00");
        testDataMocker.targetDataBuilder("2", "Success", "0.0002").updateTime("2019-10-01 02:00:00");
        // 核对不一致，补单
        testDataMocker.srcDataBuilder("3", "S", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.targetDataBuilder("3", "Process", "0.01").updateTime("2019-10-01 03:00:00");
        // 少数据，不补单
        testDataMocker.srcDataBuilder("4", "S", "0.01").updateTime("2019-10-01 04:00:00");
        // 少数据，补单
        testDataMocker.srcDataBuilder("5", "P", "0.01").updateTime("2019-10-01 05:00:00");
        testDataMocker.mock();

        ManualCompareRequest request =
            requestTemplate("2019-10-01 00:00:00", "2019-10-02 00:00:00", defineIdCompensate);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        Thread.sleep(60000);

        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().compensate(TaskStatusEnum.RETRY_WAIT, "pass: 1, lack: 2, mismatch: 2").detailCount(4);
        taskChecker.detailChecker(0).identity("4")
            .lackTarget("order_no=4, status=S, amount=0.0100, update_time=2019-10-01T04:00:00+04");
        taskChecker.detailChecker(1).identity("5").lackTargetCompensate(
            "order_no=5, status=P, amount=0.0100, update_time=2019-10-01T05:00:00+04", CompensateFlagEnum.SUCCESS);
        taskChecker.detailChecker(2).identity("2").mismatch(
            "order_no=2, status=F, amount=0.0002, update_time=2019-10-01T02:00:00+04",
            "order_no=2, status=Success, amount=0.0002, update_time=2019-10-01T02:00:00+04",
            "status[F] and status[Success] not correspond, rule: [F] -> [Fail,Failure]");
        taskChecker.detailChecker(3).identity("3").mismatchCompensate(
            "order_no=3, status=S, amount=0.0100, update_time=2019-10-01T03:00:00+04",
            "order_no=3, status=Process, amount=0.0100, update_time=2019-10-01T03:00:00+04",
            "status[S] and status[Process] not correspond, rule: [S] -> [Success]", CompensateFlagEnum.SUCCESS, "S1");

        // 重试对账，补偿时间触发
        testDataMocker.reset(DATA_SET_COMPENSATE);
        testDataMocker.mock();
        compensationEventRetryService.retry(0, 1, null);
        taskExecutorMocker.waitClear();
        taskChecker = compareTaskCheckerFactory.create(response.getResult()).exist()
            .compensate(TaskStatusEnum.RETRY_SUCCESS, "pass: 1, lack: 2, mismatch: 2").detailCount(4);
    }

    @Test
    public void test_error_srcDatasourceCode() {
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(DATA_SET_EMPTY);
        defineBuilder
            .src("order_no", 60,
                "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '"
                    + DATA_SET_EMPTY + "' and data_type = 'S'"
                    + " and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}")
            .srcDatasourceCode(TestConstants.DATASOURCE_CODE_NOAUTH);
        defineBuilder.target("order_no",
            "select order_no, status, amount, update_time" + " from csc.t_test_data where data_set = '" + DATA_SET_EMPTY
                + "' and data_type = 'T'" + " and order_no in {id_s}");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "  $data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n"
            + "  $data.isEqual('amount', 'amount')");
        long defineId = compareDefineMocker.mock(defineBuilder);

        ManualCompareRequest request = requestTemplate("2019-10-01 01:00:00", "2019-10-01 04:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist()
            .errorPrefixMessage(CommonReturnCode.SYSTEM_ERROR.getCode(),
                "Failed to obtain JDBC Connection; nested exception is java.sql.SQLException: Access denied for user ")
            .detailCount(0);
    }

    public static ManualCompareRequest requestTemplate(String beginTime, String endTime, long defineId) {
        ManualCompareRequest request = new ManualCompareRequest();
        try {
            request.setDataBeginTime(DATE_FORMAT.parse(beginTime));
            request.setDataEndTime(DATE_FORMAT.parse(endTime));
        } catch (ParseException e) {
            throw new RuntimeException("测试时间格式异常", e);
        }
        request.setDefineId(defineId);
        request.setOperator(CLIENT_ID);
        request.setClientId(CLIENT_ID);
        return request;
    }

}
