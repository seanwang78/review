package com.uaepay.pub.csc.manual;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.service.facade.MonitorTaskFacade;
import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;
import com.uaepay.pub.csc.service.facade.request.ManualMonitorRequest;
import com.uaepay.pub.csc.test.base.ManualTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.builder.NotifyRelationBuilder;
import com.uaepay.pub.csc.test.checker.monitor.MonitorTaskCheckerFactory;
import com.uaepay.pub.csc.test.domain.NotifyDefine;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.NotifyRelationMocker;
import com.uaepay.pub.csc.test.mocker.TaskExecutorMocker;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

@Disabled
public class MonitorTaskManualTest extends ManualTestBase {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Reference
    MonitorTaskFacade monitorTaskFacade;

    @Autowired
    TaskExecutorMocker taskExecutorMocker;

    @Autowired
    MonitorMocker monitorMocker;

    @Autowired
    MonitorTaskCheckerFactory monitorTaskCheckerFactory;

    @Autowired
    TestDataMocker testDataMocker;

    @Autowired
    NotifyRelationMocker notifyRelationMocker;

    @Test
    public void prepareAlarm() {
        NotifyRelationBuilder notifyBuilder = new NotifyRelationBuilder();
        String alarmExpression = "$!result.levelByCount(1,3)";

        long defineId = monitorMocker.mock(new MonitorDefineBuilder("manual_alarm")
            .alarm(TestConstants.MONITOR_ALARM_STATUS_MONGO_FIND_DIFFERENT_FIELDS, null, alarmExpression).mongo());

        System.out.println("defineId: " + defineId);
        NotifyDefine define = notifyBuilder.buildDefine(DefineTypeEnum.MONITOR, defineId);
        NotifyGroup group = notifyBuilder.buildGroup("test");
        group.setTeamsUrl(
            "https://42pay.webhook.office.com/webhookb2/a208d5a3-5e50-48b5-907c-07a7fb308f9c@0b96185d-2506-4ba7-91f6-c5de2ad22b85/IncomingWebhook/e8f81d70462c4f1996a2cbb5acdbca90/97d43267-b755-44fc-b5e9-50a3108fcce9");
        NotifyContact contact =
            // notifyBuilder.contactBuilder("xyb", "yibing.xia@payby.com").mobile("+971-585798389").build();
            notifyBuilder.contactBuilder("zc", "cong.zhou@payby.com").build();
        notifyBuilder.relate(group, contact).relate(define, group);
        notifyRelationMocker.mock(notifyBuilder);
    }

    @Test
    public void prepareReport() {
        NotifyRelationBuilder notifyBuilder = new NotifyRelationBuilder();

        long defineId = monitorMocker.mock(
            new MonitorDefineBuilder("manual_report").report(TestConstants.MONITOR_ALARM_STATUS_SQL).notifyTeams());
        System.out.println("defineId: " + defineId);
        NotifyDefine define = notifyBuilder.buildDefine(DefineTypeEnum.MONITOR, defineId);
        NotifyGroup group = notifyBuilder.buildGroup("test");
        group.setTeamsUrl(
            "https://42pay.webhook.office.com/webhookb2/a208d5a3-5e50-48b5-907c-07a7fb308f9c@0b96185d-2506-4ba7-91f6-c5de2ad22b85/IncomingWebhook/54700c7f45ab4210b98d52b29e29ee41/97d43267-b755-44fc-b5e9-50a3108fcce9");
        NotifyContact contact = notifyBuilder.contactBuilder("zc", "cong.zhou@payby.com").build();
        notifyBuilder.relate(group, contact).relate(define, group);

        notifyRelationMocker.mock(notifyBuilder);
    }

    @Test
    public void prepareData() {
        // 准备mock数据
        testDataMocker.reset(TestConstants.MONITOR_ALARM_STATUS);
        testDataMocker.monitorDataBuilder("1", "E", "0.0001").updateTime("2020-02-01 00:10:00").currency("AED");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2020-02-01 00:20:00");
        testDataMocker.monitorDataBuilder("3", "E", "0.0004").updateTime("2020-02-01 00:30:00").group1("g1");
        // testDataMocker.monitorDataBuilder("4", "E", "0.0002").updateTime("2020-02-04 00:00:00");
        // testDataMocker.monitorDataBuilder("5", "E", "0.0004").updateTime("2020-02-05 00:00:00");
        // testDataMocker.monitorDataBuilder("6", "E", "0.0002").updateTime("2020-02-06 00:00:00");
        // testDataMocker.monitorDataBuilder("7", "E", "0.0004").updateTime("2020-02-07 00:00:00");
        testDataMocker.mockMongo();
    }

    @Test
    public void testMonitor() {
        // 准备请求
        long defineId = 933;
        ManualMonitorRequest request = requestTemplate("2020-02-01 00:00:00", "2020-02-02 00:00:00", defineId);

        ObjectQueryResponse<Long> response = monitorTaskFacade.applyManual(request);
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertNotNull(response.getResult());

        taskExecutorMocker.waitClear();
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
