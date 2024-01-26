package com.uaepay.pub.csc.test.checker.monitor;

import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorScheduleMapper;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorTaskMapper;
import com.uaepay.pub.csc.test.dal.MonitorTestMapper;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareScheduleMapper;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareTaskMapper;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;
import com.uaepay.pub.csc.test.checker.compare.CompareTaskCheckerFactory;
import com.uaepay.pub.csc.test.dal.CompareTestMapper;

@Service
public class MonitorScheduleCheckerFactory {

    @Autowired
    MonitorScheduleMapper monitorScheduleMapper;

    @Autowired
    MonitorTestMapper monitorTestMapper;

    @Autowired
    MonitorTaskMapper monitorTaskMapper;

    @Autowired
    MonitorTaskCheckerFactory monitorTaskCheckerFactory;

    public ScheduleChecker create(long scheduleId) {
        MonitorSchedule schedule = monitorScheduleMapper.selectByPrimaryKey(scheduleId);
        return new ScheduleChecker(schedule);
    }

    public class ScheduleChecker {

        public ScheduleChecker(MonitorSchedule schedule) {
            this.schedule = schedule;
        }

        private MonitorSchedule schedule;

        private List<MonitorTask> taskList;

        public MonitorSchedule getScheduler() {
            return schedule;
        }

        public ScheduleChecker exist() {
            Assertions.assertNotNull(schedule);
            return this;
        }

        public ScheduleChecker checked(DateTime expectCheckedTime) {
            Assertions.assertEquals(expectCheckedTime.toDate(), schedule.getCheckedTime());
            return this;
        }

        public ScheduleChecker nextTrigger(DateTime expectNextTriggerTime) {
            Assertions.assertEquals(expectNextTriggerTime.toDate(), schedule.getNextTriggerTime());
            return this;
        }

        public ScheduleChecker version(long expectVersion) {
            Assertions.assertEquals(expectVersion, schedule.getUpdateVersion().longValue());
            return this;
        }

        public ScheduleChecker errorRetry(int expectErrorCount) {
            Assertions.assertEquals(expectErrorCount, schedule.getErrorCount().intValue());
            Assertions.assertEquals(ScheduleStatusEnum.YES, schedule.getScheduleStatus());
            return this;
        }

        public ScheduleChecker errorDisabled(int expectErrorCount) {
            Assertions.assertEquals(expectErrorCount, schedule.getErrorCount().intValue());
            Assertions.assertEquals(ScheduleStatusEnum.ERROR, schedule.getScheduleStatus());
            return this;
        }

        public ScheduleChecker taskCount(int expectTaskCount) {
            initTaskList();
            Assertions.assertEquals(expectTaskCount, taskList.size());
            return this;
        }

        public ScheduleChecker currentTaskNull() {
            Assertions.assertNull(schedule.getCurrentTaskId());
            return this;
        }

        public ScheduleChecker currentTaskIndex(int expectTaskIndex) {
            initTaskList();
            Assertions.assertEquals(taskList.get(expectTaskIndex).getTaskId(), schedule.getCurrentTaskId());
            return this;
        }

        public ScheduleChecker currentTask(long expectCurrentTaskId) {
            Assertions.assertEquals(expectCurrentTaskId, schedule.getCurrentTaskId().longValue());
            return this;
        }

        public MonitorTaskCheckerFactory.TaskChecker taskChecker(int taskIndex) {
            initTaskList();
            return monitorTaskCheckerFactory.create(taskList.get(taskIndex));
        }

        private void initTaskList() {
            if (taskList == null) {
                taskList = monitorTestMapper.selectTaskBySchedule("schedule-" + schedule.getScheduleId());
            }
        }

    }

}
