package com.uaepay.pub.csc.cases.facade.compare;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.compensation.event.domainservice.CompensationEventRetryService;
import com.uaepay.pub.csc.service.facade.CompareTaskFacade;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.CompareDefineMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class CompareTaskFacadeApplyManualEsTest extends MockTestBase {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final String DATA_SET = "unittest-es_manual";

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

    private long defineId;

    @BeforeAll
    public void setUp() {
        // 无补单对账定义
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(DATA_SET);
        defineBuilder.src(TestConstants.DATASOURCE_ES, "orderNo", 60, "{\"index\": \"i_dev_csc_test_data\""
            + ", \"query\": { \"bool\": { \"filter\": [ {\"match\": {\"dataSet\": \"" + DATA_SET + "\"}}"
            + ", {\"match\": {\"dataType\": \"S\"}}"
            + ", {\"range\": {\"updateTime\": {\"gte\": {begin}, \"lt\": {end}}}} ]}}"
            + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"], \"sort\": {\"updateTime\": \"asc\"}}");
        defineBuilder.target(TestConstants.DATASOURCE_ES, "orderNo",
            "{\"index\": \"i_dev_csc_test_data\"" + ", \"query\": {\"bool\": {\"filter\": [{\"match\": {\"dataSet\": \""
                + DATA_SET + "\"}}" + ", {\"match\": {\"dataType\": \"T\"}}, {\"terms\": {\"orderNo\": {id_s}}} ]}}"
                + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"] }");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "  $data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n"
            + "  $data.isEqual('amount', 'amount')");
        defineId = compareDefineMocker.mock(defineBuilder);
    }

    @Test
    public void test_success() throws ParseException {
        testDataMocker.reset(DATA_SET);
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.67").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.targetDataBuilder("a", "Success", "0.0001");
        testDataMocker.targetDataBuilder("b", "Fail", "123456789012345.67");
        testDataMocker.targetDataBuilder("c", "Failure", "0.01");
        testDataMocker.mockEs();

        ManualCompareRequest request = requestTemplate("2019-10-01 01:00:00", "2019-10-01 04:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success("pass: 3").detailCount(0);
    }

    @Test
    public void test_fail() throws ParseException {
        testDataMocker.reset(DATA_SET);
        testDataMocker.srcDataBuilder("1", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("2", "F", "0.0002").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("3", "F", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.srcDataBuilder("4", "S", "0.01").updateTime("2019-10-01 03:10:00");
        testDataMocker.targetDataBuilder("1", "Success", "0.0001").updateTime("2019-10-01 00:00:00");
        testDataMocker.targetDataBuilder("2", "Success", "0.0002").updateTime("2019-10-01 00:00:00");
        testDataMocker.targetDataBuilder("3", "Success", "0.02").updateTime("2019-10-01 00:00:00");
        testDataMocker.mockEs();

        ManualCompareRequest request = requestTemplate("2019-10-01 01:00:00", "2019-10-01 04:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail("pass: 1, lack: 1, mismatch: 2").detailCount(3);
        taskChecker.detailChecker(0).identity("4").lackTarget(
            "_id=unittest-es_manual_S_4_S, amount=0.01, orderNo=4, updateTime=2019-09-30T23:10:00.000Z, status=S");
        // .lackTarget("orderNo=4, status=S, amount=0.01, updateTime=2019-10-01T03:10:00+04");
        taskChecker.detailChecker(1).identity("2").mismatch(
            "_id=unittest-es_manual_S_2_F, amount=0.00020, orderNo=2, updateTime=2019-09-30T22:30:00.000Z, status=F",
            "_id=unittest-es_manual_T_2_Success, amount=0.00020, orderNo=2, updateTime=2019-09-30T20:00:00.000Z, status=Success",
            // "orderNo=2, status=F, amount=0.0002, updateTime=2019-10-01T02:30:00+04",
            // "orderNo=2, status=Success, amount=0.0002, updateTime=2019-10-01T00:00:00+04",
            "status[F] and status[Success] not correspond, rule: [F] -> [Fail,Failure]");
        taskChecker.detailChecker(2).identity("3").mismatch(
            "_id=unittest-es_manual_S_3_F, amount=0.01, orderNo=3, updateTime=2019-09-30T23:00:00.000Z, status=F",
            "_id=unittest-es_manual_T_3_Success, amount=0.02, orderNo=3, updateTime=2019-09-30T20:00:00.000Z, status=Success",
            // "orderNo=3, status=F, amount=0.01, updateTime=2019-10-01T03:00:00+04",
            // "orderNo=3, status=Success, amount=0.02, updateTime=2019-10-01T00:00:00+04",
            "status[F] and status[Success] not correspond, rule: [F] -> [Fail,Failure], amount[0.01] and amount[0.02] not equal");
    }

    public static ManualCompareRequest requestTemplate(String beginTime, String endTime, long defineId)
        throws ParseException {
        ManualCompareRequest request = new ManualCompareRequest();
        request.setDataBeginTime(DATE_FORMAT.parse(beginTime));
        request.setDataEndTime(DATE_FORMAT.parse(endTime));
        request.setDefineId(defineId);
        request.setOperator(CLIENT_ID);
        request.setClientId(CLIENT_ID);
        return request;
    }

}
