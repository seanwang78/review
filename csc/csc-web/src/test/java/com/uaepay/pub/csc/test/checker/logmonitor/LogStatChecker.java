package com.uaepay.pub.csc.test.checker.logmonitor;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.service.facade.enums.LogStatStatusEnum;

public class LogStatChecker {
    LogStat logStat;

    public LogStatChecker(LogStat logStat) {
        this.logStat = logStat;
    }

    public LogStatChecker baseInfo(String appCode, String apiCode, String returnCode, String returnMsg) {
        return appCode(appCode).apiCode(apiCode).returnCode(returnCode).returnMsg(returnMsg);
    }

    public LogStatChecker appCode(String appCode) {
        Assertions.assertEquals(appCode, logStat.getAppCode());
        return this;
    }

    public LogStatChecker apiCode(String apiCode) {
        Assertions.assertEquals(apiCode, logStat.getApiCode());
        return this;
    }

    public LogStatChecker returnCode(String returnCode) {
        Assertions.assertEquals(returnCode, logStat.getReturnCode());
        return this;
    }

    public LogStatChecker returnMsg(String returnMsg) {
        Assertions.assertEquals(returnMsg, logStat.getReturnMsg());
        return this;
    }

    public LogStatChecker lastTid(String lastTid) {
        Assertions.assertEquals(lastTid, logStat.getLatestTid());
        return this;
    }

    public LogStatChecker status(LogStatStatusEnum status) {
        Assertions.assertEquals(status.getCode(), logStat.getStatus());
        return this;
    }

    public LogStatChecker status(String status) {
        Assertions.assertEquals(status, logStat.getStatus());
        return this;
    }

    public LogStatChecker count(int count) {
        Assertions.assertEquals(count, logStat.getValueCount());
        return this;
    }

    public LogStatChecker updateBy(String updateBy) {
        Assertions.assertEquals(updateBy, logStat.getUpdateBy());
        return this;
    }

    public LogStatChecker beginTime(Date beginTime) {
        Assertions.assertEquals(beginTime, logStat.getBeginTime());
        return this;
    }

    public LogStatChecker endTime(Date endTime) {
        Assertions.assertEquals(endTime, logStat.getEndTime());
        return this;
    }

    public LogStatChecker beginTime(DateTime beginTime) {
        Assertions.assertEquals(beginTime.toDate(), logStat.getBeginTime());
        return this;
    }

    public LogStatChecker endTime(DateTime endTime) {
        Assertions.assertEquals(endTime.toDate(), logStat.getEndTime());
        return this;
    }
}
