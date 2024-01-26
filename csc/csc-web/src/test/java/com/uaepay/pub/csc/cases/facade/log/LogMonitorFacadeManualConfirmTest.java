package com.uaepay.pub.csc.cases.facade.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.service.facade.LogMonitorFacade;
import com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum;
import com.uaepay.pub.csc.service.facade.enums.LogStatStatusEnum;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmLogMonitorRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.checker.logmonitor.LogStatsCheckerFactory;
import com.uaepay.pub.csc.test.mocker.LogRuleMocker;
import com.uaepay.pub.csc.test.mocker.LogStatMocker;
import com.uaepay.unittest.facade.mocker.testtool.checker.CommonResponseChecker;

public class LogMonitorFacadeManualConfirmTest extends MockTestBase {

    /**
     * 基准时间
     */
    static final DateTime BEGIN_TIME, END_TIME;

    static {
        BEGIN_TIME = new DateTime(2038, 1, 1, 0, 0, 0);
        END_TIME = BEGIN_TIME.plusDays(7);
    }

    @Reference
    LogMonitorFacade logMonitorFacade;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    LogStatMocker logStatMocker;

    @Autowired
    LogRuleMocker logRuleMocker;

    @Autowired
    LogStatsCheckerFactory logStatsCheckerFactory;

    ManualConfirmLogMonitorRequest request;
    CommonResponseChecker checker;
    LogStatsCheckerFactory.Checker statChecker;

    @Test
    public void testInvalidParameter() {
        request = template();
        request.setFunctionCode(null);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "must not be blank: functionCode");

        request = template();
        request.setLogStatIdList(null);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "must not be empty: logStatIdList");

        request = template();
        request.setLogStatIdList(new ArrayList<>());
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "must not be empty: logStatIdList");

        request = template();
        request.setOperator(null);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "${validate.notBlank}: operator");

        request = template();
        request.setFunctionCode(WHATEVER);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "functionCode is not valid");
    }

    @Test
    public void testSuccessAndFail() {
        logStatMocker.reset();
        logStatMocker.addBuilder().info("app1", "api1", "code1", "msg1").time(BEGIN_TIME, END_TIME)
            .status(LogStatStatusEnum.RETRY).id(1L);
        logStatMocker.addBuilder().info("app2", "api2", "code2", "msg2").time(BEGIN_TIME, END_TIME)
            .status(LogStatStatusEnum.INIT).id(2L);
        logStatMocker.addBuilder().info("app3", "api3", "code3", "msg3").time(BEGIN_TIME, END_TIME)
            .status(LogStatStatusEnum.MANUAL).id(3L);
        logStatMocker.addBuilder().info("app4", "api4", "code4", "msg4").time(BEGIN_TIME, END_TIME)
            .status(LogStatStatusEnum.INIT).id(4L);
        logStatMocker.mock();

        request = template();
        request.setLogStatIdList(Arrays.asList(1L, 2L, 3L));
        request.setOperator(OPERATOR);
        invoke(request).success(null, "Update count success: 1, fail: 2");

        statChecker = logStatsCheckerFactory.create().count(4);
        statChecker.index(0).baseInfo("app1", "api1", "code1", "msg1").status(LogStatStatusEnum.RETRY).updateBy(null);
        statChecker.index(1).baseInfo("app2", "api2", "code2", "msg2").status(LogStatStatusEnum.MANUAL)
            .updateBy(OPERATOR);
        statChecker.index(2).baseInfo("app3", "api3", "code3", "msg3").status(LogStatStatusEnum.MANUAL).updateBy(null);
        statChecker.index(3).baseInfo("app4", "api4", "code4", "msg4").status(LogStatStatusEnum.INIT).updateBy(null);
    }

    private ManualConfirmLogMonitorRequest template() {
        ManualConfirmLogMonitorRequest request = new ManualConfirmLogMonitorRequest();
        request.setLogStatIdList(Collections.singletonList(0L));
        request.setFunctionCode(LogRuleFunctionCodeEnum.CGS_LOG_MONITOR.getCode());
        request.setClientId(CLIENT_ID);
        request.setOperator(OPERATOR);
        return request;
    }

    private CommonResponseChecker invoke(ManualConfirmLogMonitorRequest request) {
        return new CommonResponseChecker(logMonitorFacade.manualConfirm(request));
    }

}
