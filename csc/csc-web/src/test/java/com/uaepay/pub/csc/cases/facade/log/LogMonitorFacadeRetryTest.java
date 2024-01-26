package com.uaepay.pub.csc.cases.facade.log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.domainservice.logmonitor.strategy.impl.CgsLogMonitorStrategy;
import com.uaepay.pub.csc.service.facade.LogMonitorFacade;
import com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum;
import com.uaepay.pub.csc.service.facade.enums.LogRuleTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.LogStatStatusEnum;
import com.uaepay.pub.csc.service.facade.request.RetryLogMonitorRequest;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.checker.logmonitor.LogStatsCheckerFactory;
import com.uaepay.pub.csc.test.mocker.LogRuleMocker;
import com.uaepay.pub.csc.test.mocker.LogStatMocker;
import com.uaepay.unittest.facade.mocker.testtool.checker.CommonResponseChecker;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogMonitorFacadeRetryTest extends MockTestBase {

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

    RetryLogMonitorRequest request;
    CommonResponseChecker checker;
    LogStatsCheckerFactory.Checker statChecker;

    @Test
    public void testInvalidParameter() {
        request = template();
        request.setFunctionCode(null);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "must not be blank: functionCode");

        request = template();
        request.setBeginTime(null);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "must not be null: beginTime");

        request = template();
        request.setEndTime(null);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "must not be null: endTime");

        request = template();
        request.setOperator(null);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "${validate.notBlank}: operator");

        request = template();
        request.setFunctionCode(WHATEVER);
        checker = invoke(request).fail(CommonReturnCode.INVALID_PARAMETER, "functionCode is not valid");
    }

    @Test
    public void testLocked() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new LockTask()).get();
        invoke(template()).fail(CommonReturnCode.BIZ_CHECK_FAIL, "Retry is processing, please try again later.");
        executorService.submit(new UnlockTask()).get();
    }

    @Test
    public void testNormalWith2Page() {
        logStatMocker.reset();
        logStatMocker.addBuilder().info("app1", "api1", "409", "In white list").time(BEGIN_TIME, END_TIME);
        logStatMocker.addBuilder().info("app1", "api1", "409", "Not in list").time(BEGIN_TIME, END_TIME);
        logStatMocker.addBuilder().info("app2", "api2", "407", "In white list too").time(BEGIN_TIME, END_TIME);
        logStatMocker.mock();

        logRuleMocker.reset();
        logRuleMocker.cgsMonitorRule(LogRuleTypeEnum.WHITE).appCode("app1").apiCode("api1").code("409")
            .textExpression("In white list");
        logRuleMocker.cgsMonitorRule(LogRuleTypeEnum.WHITE).appCode("app2").code("407")
            .regexExpression("In white list.*");
        logRuleMocker.mock();

        request = template();
        request.setOperator(OPERATOR);
        invoke(request).success(null, "Retry success count: 2");

        statChecker = logStatsCheckerFactory.create().count(3);
        statChecker.index(0).baseInfo("app1", "api1", "409", "In white list").status(LogStatStatusEnum.RETRY)
            .updateBy(OPERATOR);
        statChecker.index(1).baseInfo("app1", "api1", "409", "Not in list").status(LogStatStatusEnum.INIT)
            .updateBy(null);
        statChecker.index(2).baseInfo("app2", "api2", "407", "In white list too").status(LogStatStatusEnum.RETRY)
            .updateBy(OPERATOR);
    }

    public class LockTask implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            RLock lock = redissonClient.getLock(CgsLogMonitorStrategy.LOCK_KEY);
            boolean locked = lock.tryLock();
            Assertions.assertTrue(locked);
            log.info("locked");
        }
    }

    public class UnlockTask implements Runnable {
        @Override
        public void run() {
            RLock lock = redissonClient.getLock(CgsLogMonitorStrategy.LOCK_KEY);
            lock.unlock();
            log.info("unlocked");
        }
    }

    private RetryLogMonitorRequest template() {
        RetryLogMonitorRequest request = new RetryLogMonitorRequest();
        request.setBeginTime(BEGIN_TIME.toDate());
        request.setEndTime(END_TIME.toDate());
        request.setFunctionCode(LogRuleFunctionCodeEnum.CGS_LOG_MONITOR.getCode());
        request.setClientId(CLIENT_ID);
        request.setOperator(OPERATOR);
        return request;
    }

    private CommonResponseChecker invoke(RetryLogMonitorRequest request) {
        return new CommonResponseChecker(logMonitorFacade.applyRetry(request));
    }

}
