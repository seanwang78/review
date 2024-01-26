package com.uaepay.pub.csc.test.checker.monitor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTaskDetail;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorTaskMapper;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.MonitorTaskStatusEnum;
import com.uaepay.pub.csc.test.dal.MonitorTestMapper;

@Service
public class MonitorTaskCheckerFactory {

    @Autowired
    MonitorTaskMapper monitorTaskMapper;

    @Autowired
    MonitorTestMapper monitorTestMapper;

    public TaskChecker create(long taskId) {
        MonitorTask task = monitorTaskMapper.selectByPrimaryKey(taskId);
        return new TaskChecker(task);
    }

    public TaskChecker create(MonitorTask task) {
        return new TaskChecker(task);
    }

    public class TaskChecker {

        public TaskChecker(MonitorTask task) {
            this.task = task;
        }

        private MonitorTask task;

        private List<MonitorTaskDetail> details;

        public MonitorTask getTask() {
            return task;
        }

        public TaskChecker notExist() {
            Assertions.assertNull(task);
            return this;
        }

        public TaskChecker exist() {
            Assertions.assertNotNull(task);
            return this;
        }

        public TaskChecker time(DateTime expectDataBeginTime, DateTime expectDataEndTime) {
            Assertions.assertEquals(expectDataBeginTime.toDate(), task.getDataBeginTime());
            Assertions.assertEquals(expectDataEndTime.toDate(), task.getDataEndTime());
            return this;
        }

        public TaskChecker success() {
            Assertions.assertEquals(MonitorTaskStatusEnum.SUCCESS, task.getTaskStatus());
            Assertions.assertNull(task.getErrorCode());
            Assertions.assertNull(task.getErrorMessage());
            return this;
        }

        public TaskChecker fail() {
            Assertions.assertEquals(MonitorTaskStatusEnum.FAIL, task.getTaskStatus());
            Assertions.assertNull(task.getErrorCode());
            Assertions.assertNull(task.getErrorMessage());
            Assertions.assertNull(task.getAlarmLevel());
            return this;
        }

        public TaskChecker fail(AlarmLevelEnum alarmLevel) {
            Assertions.assertEquals(MonitorTaskStatusEnum.FAIL, task.getTaskStatus());
            Assertions.assertNull(task.getErrorCode());
            Assertions.assertNull(task.getErrorMessage());
            Assertions.assertEquals(alarmLevel, task.getAlarmLevel());
            return this;
        }

        public TaskChecker error(CodeEnum errorCode, String errorMessage) {
            return error(errorCode.getCode(), errorMessage);
        }

        public TaskChecker error(String errorCode, String errorMessage) {
            Assertions.assertEquals(MonitorTaskStatusEnum.ERROR, task.getTaskStatus());
            Assertions.assertEquals(errorCode, task.getErrorCode());
            Assertions.assertEquals(errorMessage, task.getErrorMessage());
            return this;
        }

        public TaskChecker errorPrefixMessage(String errorCode, String prefixMessage) {
            Assertions.assertEquals(MonitorTaskStatusEnum.ERROR, task.getTaskStatus());
            Assertions.assertEquals(errorCode, task.getErrorCode());
            Assertions.assertTrue(StringUtils.startsWith(task.getErrorMessage(), prefixMessage));
            return this;
        }

        public TaskChecker retrySuccess(Long lastRetryTaskId) {
            Assertions.assertEquals(MonitorTaskStatusEnum.RETRY_SUCCESS, task.getTaskStatus());
            Assertions.assertEquals(lastRetryTaskId, task.getLastRetryTaskId());
            return this;
        }

        public TaskChecker manualConfirmed(String errorMessage, String operator) {
            Assertions.assertEquals(MonitorTaskStatusEnum.MANUAL_CONFIRMED, task.getTaskStatus());
            Assertions.assertEquals(errorMessage + " by " + operator, task.getErrorMessage());
            return this;
        }

        public TaskChecker detailNull() {
            return detailCount(null, null, false);
        }

        public TaskChecker detailCount(Integer detailCount) {
            return detailCount(detailCount, YesNoEnum.YES, false);
        }

        public TaskChecker detailCountMore(Integer detailCount) {
            return detailCount(detailCount, YesNoEnum.NO, false);
        }

        public TaskChecker recordDetailCount(Integer detailCount) {
            return detailCount(detailCount, YesNoEnum.YES, true);
        }

        public TaskChecker recordDetailCountMore(Integer detailCount) {
            return detailCount(detailCount, YesNoEnum.NO, true);
        }

        public TaskChecker detailCount(Integer detailCount, YesNoEnum isAllDetail, boolean recordDetail) {
            Assertions.assertEquals(detailCount, task.getDetailCount());
            Assertions.assertEquals(isAllDetail, task.getIsAllDetail());
            if (details == null) {
                details = monitorTestMapper.selectDetailByTaskId(task.getTaskId());
            }
            if (detailCount != null) {
                if (recordDetail) {
                    Assertions.assertEquals(detailCount.intValue(), details.size());
                } else {
                    Assertions.assertEquals(0, details.size());
                }
            }
            return this;
        }

        public MonitorTaskDetailChecker detailChecker(int index) {
            return new MonitorTaskDetailChecker(details.get(index));
        }

    }

}
