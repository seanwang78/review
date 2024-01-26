package com.uaepay.pub.csc.test.builder;

import java.util.Date;

import org.joda.time.DateTime;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;
import com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum;

public class JobProgressBuilder {

    JobProgress result = new JobProgress();

    public static JobProgressBuilder cgsMonitorJob(DateTime checkedTime, int checkMinutes, int delayMinutes) {
        return new JobProgressBuilder(LogRuleFunctionCodeEnum.CGS_LOG_MONITOR.getCode(), checkedTime, checkMinutes,
            delayMinutes);
    }

    private JobProgressBuilder(String jobCode, DateTime checkedTime, int checkMinutes, int delayMinutes) {
        result.setJobCode(jobCode);
        result.setJobDesc("单元测试");
        result.setCheckedTime(checkedTime.toDate());
        result.setCheckMinutes(checkMinutes);
        result.setDelayMinutes(delayMinutes);
        result.setCreateTime(new Date());
        result.setUpdateTime(new Date());

        nextTriggerTime(checkedTime.plusMinutes(checkMinutes).plusMinutes(delayMinutes));
        version(0L);
        enableFlag(YesNoEnum.YES);
    }

    public JobProgressBuilder nextTriggerTime(DateTime nextTriggerTime) {
        result.setNextTriggerTime(nextTriggerTime.toDate());
        return this;
    }

    public JobProgressBuilder version(Long version) {
        result.setUpdateVersion(version);
        return this;
    }

    public JobProgressBuilder enableFlag(YesNoEnum enableFlag) {
        result.setEnableFlag(enableFlag.getCode());
        return this;
    }

    public JobProgressBuilder jobCode(String jobCode) {
        result.setJobCode(jobCode);
        return this;
    }

    public JobProgressBuilder disabled() {
        return enableFlag(YesNoEnum.NO);
    }

    public JobProgress build() {
        return result;
    }

}
