package com.uaepay.pub.csc.test.builder;

import java.util.Date;

import org.joda.time.DateTime;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.service.facade.enums.LogStatStatusEnum;

public class LogStatBuilder {

    LogStat result = new LogStat();

    public LogStatBuilder() {
        result.setValueCount(1L);
        result.setStatus(LogStatStatusEnum.INIT.getCode());
        result.setCreateTime(new Date());
        result.setUpdateTime(new Date());
    }

    public LogStatBuilder info(String appCode, String apiCode, String returnCode, String returnMsg) {
        result.setAppCode(appCode);
        result.setApiCode(apiCode);
        result.setReturnCode(returnCode);
        result.setReturnMsg(returnMsg);
        return this;
    }

    public LogStatBuilder time(DateTime begin, DateTime end) {
        result.setBeginTime(begin.toDate());
        result.setEndTime(end.toDate());
        return this;
    }

    public LogStatBuilder status(LogStatStatusEnum status) {
        result.setStatus(status.getCode());
        return this;
    }

    public LogStatBuilder id(Long id) {
        result.setId(id);
        return this;
    }

    public LogStat build() {
        return result;
    }

}
