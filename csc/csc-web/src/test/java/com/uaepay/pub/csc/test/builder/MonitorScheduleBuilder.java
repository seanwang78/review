package com.uaepay.pub.csc.test.builder;

import java.text.ParseException;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;

/**
 * @author zc
 */
public class MonitorScheduleBuilder {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public MonitorScheduleBuilder(long defineId, long version) {
        schedule.setDefineId(defineId);
        schedule.setUpdateVersion(version);
        schedule.setScheduleStatus(ScheduleStatusEnum.YES);
        schedule.setErrorCount(0);
    }

    private MonitorSchedule schedule = new MonitorSchedule();

    public MonitorScheduleBuilder config(String checkTime, Integer checkMinutes, Integer delayMinutes) {
        try {
            return config(new DateTime(DATE_FORMAT.parse(checkTime)), checkMinutes, delayMinutes);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MonitorScheduleBuilder config(DateTime checkTime, Integer checkMinutes, Integer delayMinutes) {
        schedule.setCheckedTime(checkTime.toDate());
        schedule.setCheckMinutes(checkMinutes);
        schedule.setDelayMinutes(delayMinutes);
        return this;
    }

    public MonitorScheduleBuilder nextTrigger(String nextTriggerTime) {
        try {
            return nextTrigger(new DateTime(DATE_FORMAT.parse(nextTriggerTime)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MonitorScheduleBuilder nextTrigger(DateTime nextTriggerTime) {
        schedule.setNextTriggerTime(nextTriggerTime.toDate());
        return this;
    }

    public MonitorScheduleBuilder errorCount(int errorCount) {
        schedule.setErrorCount(errorCount);
        return this;
    }

    public MonitorScheduleBuilder currentTask(long currentTaskId) {
        schedule.setCurrentTaskId(currentTaskId);
        return this;
    }

    public MonitorSchedule build() {
        return schedule;
    }

}
