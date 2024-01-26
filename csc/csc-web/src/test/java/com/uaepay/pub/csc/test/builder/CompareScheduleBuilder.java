package com.uaepay.pub.csc.test.builder;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;

public class CompareScheduleBuilder {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public CompareScheduleBuilder(long defineId, long version) {
        schedule.setDefineId(defineId);
        schedule.setVersion(version);
        schedule.setScheduleStatus(ScheduleStatusEnum.YES);
        schedule.setErrorCount(0);
    }

    private CompareSchedule schedule = new CompareSchedule();

    public CompareScheduleBuilder config(String checkTime, Integer checkMinutes, Integer delayMinutes) {
        try {
            return config(new DateTime(DATE_FORMAT.parse(checkTime)), checkMinutes, delayMinutes);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public CompareScheduleBuilder config(DateTime checkTime, Integer checkMinutes, Integer delayMinutes) {
        schedule.setCheckedTime(checkTime.toDate());
        schedule.setCheckMinutes(checkMinutes);
        schedule.setDelayMinutes(delayMinutes);
        return this;
    }

    public CompareScheduleBuilder nextTrigger(String nextTriggerTime) {
        try {
            return nextTrigger(new DateTime(DATE_FORMAT.parse(nextTriggerTime)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public CompareScheduleBuilder nextTrigger(DateTime nextTriggerTime) {
        schedule.setNextTriggerTime(nextTriggerTime.toDate());
        return this;
    }

    public CompareScheduleBuilder errorCount(int errorCount) {
        schedule.setErrorCount(errorCount);
        return this;
    }

    public CompareScheduleBuilder currentTask(long currentTaskId) {
        schedule.setCurrentTaskId(currentTaskId);
        return this;
    }

    public CompareSchedule build() {
        return schedule;
    }

}
