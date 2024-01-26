package com.uaepay.pub.csc.cases.domainservice.logmonitor.schedule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;
import com.uaepay.pub.csc.domainservice.logmonitor.schedule.GatewayMonitorService;
import com.uaepay.pub.csc.service.facade.enums.LogRuleTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.LogStatStatusEnum;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.JobProgressBuilder;
import com.uaepay.pub.csc.test.checker.logmonitor.JobProgressCheckerFactory;
import com.uaepay.pub.csc.test.checker.logmonitor.LogStatsCheckerFactory;
import com.uaepay.pub.csc.test.mocker.CgsLogTestDataMocker;
import com.uaepay.pub.csc.test.mocker.JobProgressMocker;
import com.uaepay.pub.csc.test.mocker.LogRuleMocker;
import com.uaepay.pub.csc.test.mocker.LogStatMocker;

public class LogMonitorServiceTest extends MockTestBase {

    private static final int CHECK_MINUTES = 10;
    private static final int DELAY_MINUTES = 1;

    @Autowired
    GatewayMonitorService gatewayMonitorService;

    @Autowired
    CgsLogTestDataMocker cgsLogTestDataMocker;

    @Autowired
    JobProgressMocker jobProgressMocker;

    @Autowired
    JobProgressCheckerFactory jobProgressCheckerFactory;

    @Autowired
    LogStatsCheckerFactory logStatsCheckerFactory;

    @Autowired
    LogStatMocker logStatMocker;

    @Autowired
    LogRuleMocker logRuleMocker;

    /** 基准时间 */
    static final DateTime BASE_TIME;

    DateTime currentTime;
    LogStatsCheckerFactory.Checker statsChecker;

    static {
        // 基准时间
        BASE_TIME = new DateTime(2038, 1, 1, 0, 0, 0);
    }

    @BeforeEach
    public void setUp() {
        currentTime = BASE_TIME.plusMinutes(CHECK_MINUTES + DELAY_MINUTES);
        DateTimeUtils.setCurrentMillisFixed(currentTime.getMillis());

        logStatMocker.clearAll();
    }

    @Test
    public void testNoJob() {
        jobProgressMocker.clear();
        gatewayMonitorService.runJob();
    }

    @Test
    public void testNoCrossDay() {
        // mock
        DateTimeUtils.setCurrentMillisFixed(BASE_TIME.plusMinutes(1441).getMillis());
        JobProgress progress =
            jobProgressMocker.mockSingle(JobProgressBuilder.cgsMonitorJob(BASE_TIME, 1441, 0).version(0L));

        // execute
        gatewayMonitorService.runJob();

        // check
        jobProgressCheckerFactory.create(progress).checkedTime(BASE_TIME).memo("checkMinutes 不能跨天").version(1L);
    }

    @Test
    public void testTriggerTimeError() {
        // mock
        DateTimeUtils.setCurrentMillisFixed(BASE_TIME.getMillis());
        JobProgress progress = jobProgressMocker.mockSingle(JobProgressBuilder
            .cgsMonitorJob(BASE_TIME, CHECK_MINUTES, DELAY_MINUTES).nextTriggerTime(BASE_TIME).version(0L));

        // execute
        gatewayMonitorService.runJob();

        // check
        jobProgressCheckerFactory.create(progress).checkedTime(BASE_TIME).memo("min trigger time not arrived")
            .version(1L);
    }

    @Test
    public void testJobCodeInvalid() {
        // mock
        JobProgress progress = jobProgressMocker.mockSingle(
            JobProgressBuilder.cgsMonitorJob(BASE_TIME, CHECK_MINUTES, DELAY_MINUTES).jobCode(WHATEVER).version(0L));

        // execute
        gatewayMonitorService.runJob();

        // check
        jobProgressCheckerFactory.create(progress).checkedTime(BASE_TIME).memo("whatever jobCode is not valid")
            .version(1L);
    }

    @Test
    public void testNoRule() {
        // mock
        currentTime = BASE_TIME.plusMinutes(CHECK_MINUTES + DELAY_MINUTES);
        DateTimeUtils.setCurrentMillisFixed(currentTime.getMillis());
        JobProgress progress = jobProgressMocker
            .mockSingle(JobProgressBuilder.cgsMonitorJob(BASE_TIME, CHECK_MINUTES, DELAY_MINUTES).version(0L));
        cgsLogTestDataMocker.reset(BASE_TIME, currentTime);
        cgsLogTestDataMocker.builder("app1", "api1", "code1", "msg1").tid("tid1").time(BASE_TIME.plusMinutes(1));
        cgsLogTestDataMocker.builder("app1", "api1", "code1", "msg1").tid("tid2").time(BASE_TIME.plusMinutes(2));
        cgsLogTestDataMocker.builder("app1", "api2", "code2", "msg2").tid("tid3").time(BASE_TIME.plusMinutes(3));
        cgsLogTestDataMocker.builder("app2", "api1", "code3", "msg3").time(BASE_TIME.plusMinutes(4));
        cgsLogTestDataMocker.mockEs();

        // execute
        gatewayMonitorService.runJob();

        // check
        statsChecker = logStatsCheckerFactory.create().count(3);
        statsChecker.index(0).baseInfo("app1", "api1", "code1", "msg1").count(2).lastTid("tid2").beginTime(BASE_TIME)
            .endTime(BASE_TIME.plusMinutes(CHECK_MINUTES)).status(LogStatStatusEnum.INIT);
        statsChecker.index(1).baseInfo("app1", "api2", "code2", "msg2").count(1).lastTid("tid3").beginTime(BASE_TIME)
            .endTime(BASE_TIME.plusMinutes(CHECK_MINUTES)).status(LogStatStatusEnum.INIT);;
        statsChecker.index(2).baseInfo("app2", "api1", "code3", "msg3").count(1).lastTid(null).beginTime(BASE_TIME)
            .endTime(BASE_TIME.plusMinutes(CHECK_MINUTES)).status(LogStatStatusEnum.INIT);
        jobProgressCheckerFactory.create(progress).checkedTime(BASE_TIME.plusMinutes(CHECK_MINUTES))
            .nextTrigger(BASE_TIME.plusMinutes(CHECK_MINUTES * 2 + DELAY_MINUTES)).memo(null).version(1L);
    }

    @Test
    public void testRunMultiTimes() {
        // mock
        currentTime = BASE_TIME.plusMinutes(CHECK_MINUTES * 2 + DELAY_MINUTES);
        DateTimeUtils.setCurrentMillisFixed(currentTime.getMillis());
        JobProgress progress = jobProgressMocker
            .mockSingle(JobProgressBuilder.cgsMonitorJob(BASE_TIME, CHECK_MINUTES, DELAY_MINUTES).version(0L));
        cgsLogTestDataMocker.reset(BASE_TIME, currentTime);
        cgsLogTestDataMocker.builder("app1", "api1", "code1", "msg1").time(BASE_TIME);
        cgsLogTestDataMocker.builder("app2", "api2", "code2", "msg2").time(BASE_TIME.plusMinutes(CHECK_MINUTES));
        cgsLogTestDataMocker.mockEs();

        // execute
        gatewayMonitorService.runJob();

        // check
        statsChecker = logStatsCheckerFactory.create().count(2);
        statsChecker.index(0).baseInfo("app1", "api1", "code1", "msg1").count(1).beginTime(BASE_TIME)
            .endTime(BASE_TIME.plusMinutes(CHECK_MINUTES)).status(LogStatStatusEnum.INIT);
        statsChecker.index(1).baseInfo("app2", "api2", "code2", "msg2").count(1)
            .beginTime(BASE_TIME.plusMinutes(CHECK_MINUTES)).endTime(BASE_TIME.plusMinutes(CHECK_MINUTES * 2))
            .status(LogStatStatusEnum.INIT);
        jobProgressCheckerFactory.create(progress).checkedTime(BASE_TIME.plusMinutes(CHECK_MINUTES * 2))
            .nextTrigger(BASE_TIME.plusMinutes(CHECK_MINUTES * 3 + DELAY_MINUTES)).memo(null).version(2L);
    }

    @Test
    public void testEndTimeCrossDay() {
        // mock
        DateTime baseTime = new DateTime(2038, 1, 1, 23, 58, 0);
        DateTime currentTime = new DateTime(2038, 1, 2, 0, 10, 0);
        DateTimeUtils.setCurrentMillisFixed(currentTime.getMillis());
        JobProgress progress =
            jobProgressMocker.mockSingle(JobProgressBuilder.cgsMonitorJob(baseTime, 10, 1).version(0L));
        cgsLogTestDataMocker.reset(baseTime, currentTime);
        cgsLogTestDataMocker.builder("app1", "api1", "code1", "msg1").tid("tid1").time(baseTime.plusMinutes(1));
        cgsLogTestDataMocker.builder("app2", "api2", "code2", "msg2").tid("tid2").time(baseTime.plusMinutes(3));
        cgsLogTestDataMocker.mockEs();

        // execute
        gatewayMonitorService.runJob();

        // check
        statsChecker = logStatsCheckerFactory.create().count(1);
        statsChecker.index(0).baseInfo("app1", "api1", "code1", "msg1").count(1).lastTid("tid1").beginTime(baseTime)
            .endTime(baseTime.plusMinutes(2)).status(LogStatStatusEnum.INIT);
        jobProgressCheckerFactory.create(progress).checkedTime(new DateTime(2038, 1, 2, 0, 0, 0))
            .nextTrigger(new DateTime(2038, 1, 2, 0, 11, 0)).memo(null).version(1L);
    }

    @Test
    public void testBlackWhiteList() {
        // mock
        DateTimeUtils.setCurrentMillisFixed(BASE_TIME.plusMinutes(CHECK_MINUTES + DELAY_MINUTES).getMillis());
        JobProgress progress = jobProgressMocker
            .mockSingle(JobProgressBuilder.cgsMonitorJob(BASE_TIME, CHECK_MINUTES, DELAY_MINUTES).version(0L));
        cgsLogTestDataMocker.reset(BASE_TIME, currentTime);
        cgsLogTestDataMocker.builder("app1", "api1", "409", "Black prior to white...").time(BASE_TIME.plusMinutes(2));
        cgsLogTestDataMocker.builder("app1", "api1", "500", "In black list").time(BASE_TIME.plusMinutes(1));
        cgsLogTestDataMocker.builder("app1", "api1", "409", "In white list").time(BASE_TIME.plusMinutes(3));
        cgsLogTestDataMocker.builder("app1", "api2", "409", "ApiCode not in white list").time(BASE_TIME.plusMinutes(5));
        cgsLogTestDataMocker.builder("app2", "api1", "409", "AppCode not in white list").time(BASE_TIME.plusMinutes(4));
        cgsLogTestDataMocker.builder("app3", "api3", "400", "Normal").time(BASE_TIME.plusMinutes(6));
        cgsLogTestDataMocker.builder("app3", "api3", "400", "Abnormal").time(BASE_TIME.plusMinutes(7));
        cgsLogTestDataMocker.builder("app4", "api4", "400", "No match").time(BASE_TIME.plusMinutes(8));
        cgsLogTestDataMocker.mockEs();

        logRuleMocker.reset();
        logRuleMocker.cgsMonitorRule(LogRuleTypeEnum.BLACK).code("500");
        logRuleMocker.cgsMonitorRule(LogRuleTypeEnum.BLACK).regexExpression("Black prior to white.*");
        logRuleMocker.cgsMonitorRule(LogRuleTypeEnum.WHITE).appCode("app1").apiCode("api1").code("409");
        logRuleMocker.cgsMonitorRule(LogRuleTypeEnum.WHITE).appCode("app3").code("400").textExpression("Normal");
        logRuleMocker.mock();

        // execute
        gatewayMonitorService.runJob();

        // check
        statsChecker = logStatsCheckerFactory.create().count(6);
        statsChecker.index(0).baseInfo("app1", "api1", "409", "Black prior to white...");
        statsChecker.index(1).baseInfo("app1", "api1", "500", "In black list");
        statsChecker.index(2).baseInfo("app1", "api2", "409", "ApiCode not in white list");
        statsChecker.index(3).baseInfo("app2", "api1", "409", "AppCode not in white list");
        statsChecker.index(4).baseInfo("app3", "api3", "400", "Abnormal");
        statsChecker.index(5).baseInfo("app4", "api4", "400", "No match");
        jobProgressCheckerFactory.create(progress).checkedTime(BASE_TIME.plusMinutes(CHECK_MINUTES))
            .nextTrigger(BASE_TIME.plusMinutes(CHECK_MINUTES * 2 + DELAY_MINUTES)).memo(null).version(1L);
    }
}
