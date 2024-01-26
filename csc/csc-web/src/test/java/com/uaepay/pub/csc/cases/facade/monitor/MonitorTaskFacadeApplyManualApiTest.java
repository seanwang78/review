package com.uaepay.pub.csc.cases.facade.monitor;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.common.util.money.Money;
import com.uaepay.pub.csc.datasource.facade.builder.RowDataListBuilder;
import com.uaepay.pub.csc.service.facade.MonitorTaskFacade;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.request.ManualMonitorRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.checker.monitor.MonitorTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.QueryDataMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;

public class MonitorTaskFacadeApplyManualApiTest extends MockTestBase {

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
    QueryDataMocker queryDataMocker;

    @Test
    public void test_queryTemplate_blank() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(WHATEVER).api().report(""));
        ManualMonitorRequest request = requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().error(CommonReturnCode.INVALID_PARAMETER, "query template not json format");
    }

    @Test
    public void test_queryTemplate_formatError() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(WHATEVER).api().report("123"));
        ManualMonitorRequest request = requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().error(CommonReturnCode.INVALID_PARAMETER, "query template not json format");
    }

    @Test
    public void test_appCode_notExist() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(WHATEVER).api().report("{}"));
        ManualMonitorRequest request = requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().error(CommonReturnCode.INVALID_PARAMETER, "appCode not exist");
    }

    @Test
    public void test_queryFail() {
        long defineId = monitorMocker.mock(
            new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS).api().report("{'appCode': 'gp056_csc'}"));
        ManualMonitorRequest request = requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);

        queryDataMocker.fixFail("mock fail");

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().error(CommonReturnCode.SYSTEM_ERROR, "数据查询异常: mock fail");
    }

    @Test
    public void test_noData() {
        long defineId = monitorMocker.mock(
            new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS).api().report("{'appCode': 'gp056_csc'}"));
        ManualMonitorRequest request = requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);

        queryDataMocker.fixSuccess(null);

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().success().detailCount(0);
    }

    @Test
    public void test_hasData() {
        long defineId = monitorMocker.mock(new MonitorDefineBuilder(TestConstants.MONITOR_REPORT_STATUS).api()
            .alarm("{'appCode': 'gp056_csc', 'dataType': 'bank_balance', 'queryParam': '123'}", null).recordDetail());
        ManualMonitorRequest request = requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);

        queryDataMocker.fixSuccess(new RowDataListBuilder().columns("accountNo", "balance", "amount")
            .addRow("aaa", new BigDecimal("12.34"), new Money("1.00", "AED"))
            .addRow("bbb", new BigDecimal("56.7890"), new Money("0.05", "USD")).build());

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        MonitorTaskCheckerFactory.TaskChecker taskChecker = monitorTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().fail(AlarmLevelEnum.NORMAL).recordDetailCount(2);
        taskChecker.detailChecker(0).identity("aaa").content("accountNo=aaa, balance=12.34, amount=AED:1.00");
        taskChecker.detailChecker(1).identity("bbb").content("accountNo=bbb, balance=56.7890, amount=USD:0.05");
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
