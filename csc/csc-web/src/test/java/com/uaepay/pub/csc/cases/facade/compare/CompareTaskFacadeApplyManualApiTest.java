package com.uaepay.pub.csc.cases.facade.compare;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.pub.csc.service.facade.CompareTaskFacade;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.mocker.CompareDefineMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;

public class CompareTaskFacadeApplyManualApiTest extends MockTestBase {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final String DATA_SET = "unittest-es_manual";

    @Autowired
    CompareDefineMocker compareDefineMocker;

    @Autowired
    CompareTaskFacade compareTaskFacade;

    @Autowired
    CompareTaskCheckerFactory compareTaskCheckerFactory;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    private long defineId;

    @BeforeAll
    public void setUp() {
        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(DATA_SET);
        defineBuilder.src(TestConstants.DATASOURCE_API, "orderNo", 60, "{}");
        defineBuilder.target(TestConstants.DATASOURCE_API, "orderNo", "{}");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "  $data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n"
            + "  $data.isEqual('amount', 'amount')");
        defineId = compareDefineMocker.mock(defineBuilder);
    }

    @Test
    public void test_not_support() throws ParseException {
        ManualCompareRequest request = requestTemplate("2021-12-15 00:00:00", "2021-12-16 00:00:00", defineId);
        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();

        CompareTaskCheckerFactory.TaskChecker taskChecker = compareTaskCheckerFactory.create(response.getResult());
        taskChecker.exist().error("CONFIG_ERROR", "data source type not supported").detailCount(0);
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
