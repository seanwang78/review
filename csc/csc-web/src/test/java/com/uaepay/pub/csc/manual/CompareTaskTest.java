package com.uaepay.pub.csc.manual;

import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.pub.csc.cases.facade.compare.CompareTaskFacadeApplyManualTest;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.service.facade.CompareTaskFacade;
import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;
import com.uaepay.pub.csc.test.base.ManualTestBase;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.builder.NotifyRelationBuilder;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.domain.NotifyDefine;
import com.uaepay.pub.csc.test.mocker.CompareDefineMocker;
import com.uaepay.pub.csc.test.mocker.NotifyRelationMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

/**
 * <pre>
 * Step 1: Prepare Compare
 * Step 2: Prepare Data
 * Step 3: Execute Compare
 * </pre>
 */
@Disabled
public class CompareTaskTest extends ManualTestBase {

    /**
     * Unit Test Manual
     */
    private static final String DATA_SET = "utm_compare";

    @Reference
    CompareTaskFacade compareTaskFacade;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    @Autowired
    CompareDefineMocker compareDefineMocker;

    @Autowired
    CompareTaskCheckerFactory compareTaskCheckerFactory;

    @Autowired
    TestDataMocker testDataMocker;

    @Autowired
    NotifyRelationMocker notifyRelationMocker;

    @Test
    public void prepareCompare() {
        NotifyRelationBuilder notifyBuilder = new NotifyRelationBuilder();

        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(DATA_SET);
        defineBuilder.src("order_no", 60,
            "select order_no, status, amount, update_time from csc.t_test_data where data_set = '" + DATA_SET
                + "' and data_type = 'S' and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}");
        defineBuilder.target("order_no",
            "select order_no, status, amount, update_time from csc.t_test_data where data_set = '" + DATA_SET
                + "' and data_type = 'T' and order_no in {id_s}");
        defineBuilder.compare("$data.isCorrespond('status', 'status', 'S', 'Success')\n"
            + "  $data.isCorrespond('status', 'status', 'F', 'Fail,Failure')\n"
            + "  $data.isEqual('amount', 'amount')");
        long defineId = compareDefineMocker.mock(defineBuilder);
        System.out.println("defineId: " + defineId);

        NotifyDefine define = notifyBuilder.buildDefine(DefineTypeEnum.COMPARE, defineId);
        NotifyGroup group = notifyBuilder.buildGroup("test");
        group.setTeamsUrl(
            "https://42pay.webhook.office.com/webhookb2/a208d5a3-5e50-48b5-907c-07a7fb308f9c@0b96185d-2506-4ba7-91f6-c5de2ad22b85/IncomingWebhook/54700c7f45ab4210b98d52b29e29ee41/97d43267-b755-44fc-b5e9-50a3108fcce9");
        NotifyContact contact =
            notifyBuilder.contactBuilder("zc", "cong.zhou@payby.com").mobile("+86-13564927488").build();
        // notifyBuilder.contactBuilder("xyb", "yibing.xia@payby.com").mobile("+971-585798389").build();
        notifyBuilder.relate(group, contact).relate(define, group);

        notifyRelationMocker.mock(notifyBuilder);
    }

    @Test
    public void prepareCompare2() {
        NotifyRelationBuilder notifyBuilder = new NotifyRelationBuilder();

        CompareDefineBuilder defineBuilder = new CompareDefineBuilder(DATA_SET);
        defineBuilder.src("order_no", 60,
            "select order_no, status, amount, update_time from csc.t_test_data where data_set = '" + DATA_SET
                + "' and data_type = 'S' and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}");
        defineBuilder.target("order_no",
            "select order_no, status, amount, update_time from csc.t_test_data where data_set = '" + DATA_SET
                + "' and data_type = 'T' and order_no in {id_s}");
        defineBuilder.compare("$data.isCorrespondAll('status', 'status', 'S', 'S1,S2')");
        long defineId = compareDefineMocker.mock(defineBuilder);
        System.out.println("defineId: " + defineId);

        NotifyDefine define = notifyBuilder.buildDefine(DefineTypeEnum.COMPARE, defineId);
        NotifyGroup group = notifyBuilder.buildGroup("test");
        NotifyContact contact =
            notifyBuilder.contactBuilder("zc", "cong.zhou@payby.com").mobile("+86-13564927488").build();
        notifyBuilder.relate(group, contact).relate(define, group);

        notifyRelationMocker.mock(notifyBuilder);
    }

    @Test
    public void prepareNormalData() {
        // 准备mock数据
        testDataMocker.reset(DATA_SET);
        // Normal
        testDataMocker.srcDataBuilder("a", "S", "0.10").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("b", "S", "1.00").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.targetDataBuilder("a", "Success", "0.1000");
        testDataMocker.targetDataBuilder("b", "Fail", "2.00");

        testDataMocker.mock();
    }

    @Test
    public void prepareCorrespondAllData() {
        // 准备mock数据
        testDataMocker.reset(DATA_SET);
        // CorrespondAll
        testDataMocker.srcDataBuilder("a", "S", "0.10").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("b", "S", "1.00").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("c", "S", "2.00").updateTime("2019-10-01 02:40:00");
        testDataMocker.targetDataBuilder("a", "S1", "0.1000");
        testDataMocker.targetDataBuilder("a", "S2", "0.1000");
        testDataMocker.targetDataBuilder("b", "S1", "1.00");
        testDataMocker.targetDataBuilder("b", "S3", "1.00");
        testDataMocker.mock();
    }

    @Test
    public void testCompare() {
        // 准备请求，修改此定义id后测试
        long defineId = 201;
        ManualCompareRequest request =
            CompareTaskFacadeApplyManualTest.requestTemplate("2019-10-01 01:00:00", "2019-10-01 04:00:00", defineId);

        ObjectQueryResponse<Long> response = compareTaskFacade.applyManualCompare(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();
    }

}
