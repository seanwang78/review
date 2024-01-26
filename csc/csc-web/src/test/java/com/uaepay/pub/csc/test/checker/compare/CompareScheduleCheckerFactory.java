package com.uaepay.pub.csc.test.checker.compare;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareScheduleMapper;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareTaskMapper;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;
import com.uaepay.pub.csc.test.dal.CompareTestMapper;

@Service
public class CompareScheduleCheckerFactory {

    @Autowired
    CompareScheduleMapper compareScheduleMapper;

    @Autowired
    CompareTestMapper compareTestMapper;

    @Autowired
    CompareTaskMapper compareTaskMapper;

    @Autowired
    CompareTaskCheckerFactory compareTaskCheckerFactory;

    public ScheduleChecker create(long scheduleId) {
        CompareSchedule schedule = compareScheduleMapper.selectByPrimaryKey(scheduleId);
        return new ScheduleChecker(schedule);
    }

    public class ScheduleChecker {

        public ScheduleChecker(CompareSchedule schedule) {
            this.schedule = schedule;
        }

        private CompareSchedule schedule;

        private List<CompareTask> compareTaskList;

        public CompareSchedule getScheduler() {
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
            Assertions.assertEquals(expectVersion, schedule.getVersion().longValue());
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
            Assertions.assertEquals(expectTaskCount, compareTaskList.size());
            return this;
        }

        public ScheduleChecker currentTaskNull() {
            Assertions.assertNull(schedule.getCurrentTaskId());
            return this;
        }

        public ScheduleChecker currentTaskIndex(int expectTaskIndex) {
            initTaskList();
            Assertions.assertEquals(compareTaskList.get(expectTaskIndex).getTaskId(), schedule.getCurrentTaskId());
            return this;
        }

        public ScheduleChecker currentTask(long expectCurrentTaskId) {
            Assertions.assertEquals(expectCurrentTaskId, schedule.getCurrentTaskId().longValue());
            return this;
        }

        public CompareTaskCheckerFactory.TaskChecker taskChecker(int taskIndex) {
            initTaskList();
            return compareTaskCheckerFactory.create(compareTaskList.get(taskIndex));
        }

        private void initTaskList() {
            if (compareTaskList == null) {
                compareTaskList = compareTestMapper.selectTaskBySchedule(schedule.getDefineId(),
                    "schedule-" + schedule.getScheduleId());
            }
        }

    }

}
