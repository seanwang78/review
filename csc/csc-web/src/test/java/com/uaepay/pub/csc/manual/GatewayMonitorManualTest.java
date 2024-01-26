package com.uaepay.pub.csc.manual;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogRule;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.JobProgressMapper;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogRuleMapper;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.LogStatRepository;
import com.uaepay.pub.csc.domainservice.logmonitor.schedule.GatewayMonitorService;
import com.uaepay.pub.csc.service.facade.LogMonitorFacade;
import com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmLogMonitorRequest;
import com.uaepay.pub.csc.service.facade.request.RetryLogMonitorRequest;
import com.uaepay.pub.csc.test.base.ManualTestBase;

@Disabled
public class GatewayMonitorManualTest extends ManualTestBase {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Autowired
    GatewayMonitorService gatewayMonitorService;

    @Autowired
    JobProgressMapper jobProgressMapper;

    @Autowired
    LogRuleMapper logRuleMapper;

    @Autowired
    LogStatRepository logStatRepository;

    @Reference
    LogMonitorFacade logMonitorFacade;

    @Test
    public void testMonitor() {
        gatewayMonitorService.runJob();
    }

    @Test
    public void jobProgressTest() throws ParseException {
        JobProgress jobProgress = new JobProgress();
        jobProgress.setJobCode("CGS_LOG_MONITOR");
        jobProgress.setJobDesc("cgs 日终监控");
        jobProgress.setCheckedTime(DATE_FORMAT.parse("2021-12-27 00:00:00"));
        jobProgress.setCheckMinutes(60);
        jobProgress.setDelayMinutes(1);
        jobProgress.setNextTriggerTime(new Date());
        jobProgress.setEnableFlag("1");
        jobProgress.setCreateTime(new Date());
        jobProgress.setUpdateTime(new Date());
        jobProgressMapper.insertSelective(jobProgress);
    }

    @Test
    public void logRuleTest() throws ParseException {
        LogRule whiteRule = new LogRule();
        whiteRule.setFunctionCode("CGS_LOG_MONITOR");
        whiteRule.setRuleType("WHITE");
        whiteRule.setExpressionType("REGEX");
        whiteRule.setMatchExpression("Something went wrong.");

        whiteRule.setEnableFlag(YesNoEnum.YES.getCode());
        whiteRule.setCreateTime(new Date());
        whiteRule.setUpdateTime(new Date());
        logRuleMapper.insert(whiteRule);

        // LogRule blackRule = new LogRule();
        // blackRule.setFunctionCode("CGS_LOG_MONITOR");
        // blackRule.setReturnCode("500");
        // blackRule.setRuleType("BLACK");
        // blackRule.setExpressionType("NONE");
        //
        // blackRule.setEnableFlag(YesNoEnum.YES.getCode());
        // blackRule.setCreateTime(new Date());
        // blackRule.setUpdateTime(new Date());
        // logRuleMapper.insert(blackRule);

    }

    @Test
    public void selectToRetry() throws ParseException {
        List<LogStat> list = logStatRepository.selectToRetry(DATE_FORMAT.parse("2021-12-27 00:00:00"),
            DATE_FORMAT.parse("2021-12-28 00:00:00"), 0L, 30);
        System.out.println(list);

        Assertions.assertTrue(list.size() == 30);
    }

    @Test
    public void manualConfirm() {

        List<Long> idList = new ArrayList<>();
        idList.add(2L);
        idList.add(3L);
        idList.add(4L);

        int result = logStatRepository.updateManualConfirm(idList, "operatorTest");
        Assertions.assertTrue(result == 0);

    }

    @Test
    public void retry() throws ParseException, InterruptedException {
        RetryLogMonitorRequest request = new RetryLogMonitorRequest();
        request.setFunctionCode(LogRuleFunctionCodeEnum.CGS_LOG_MONITOR.getCode());

        request.setBeginTime(DATE_FORMAT.parse("2021-12-29 00:00:00"));
        request.setEndTime(DATE_FORMAT.parse("2021-12-31 00:00:00"));
        request.setOperator("test1");
        request.setClientId("unittest");
        logMonitorFacade.applyRetry(request);
        Thread.sleep(200000);
    }

    @Test
    public void manualConfirmTest() throws ParseException {
        ManualConfirmLogMonitorRequest request = new ManualConfirmLogMonitorRequest();
        request.setFunctionCode(LogRuleFunctionCodeEnum.CGS_LOG_MONITOR.getCode());
        List<Long> idList = new ArrayList<>();
        idList.add(2L);
        idList.add(3L);
        idList.add(4L);
        request.setLogStatIdList(idList);
        request.setOperator("test1");
        request.setClientId("unittest");
        logMonitorFacade.manualConfirm(request);
    }
}
