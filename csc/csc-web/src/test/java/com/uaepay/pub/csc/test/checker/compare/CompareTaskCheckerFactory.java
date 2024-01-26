package com.uaepay.pub.csc.test.checker.compare;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareTaskMapper;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;
import com.uaepay.pub.csc.test.dal.CompareTestMapper;

@Service
public class CompareTaskCheckerFactory {

    @Autowired
    CompareTaskMapper compareTaskMapper;

    @Autowired
    CompareTestMapper compareTestMapper;

    public TaskChecker create(long taskId) {
        CompareTask task = compareTaskMapper.selectByPrimaryKey(taskId);
        return new TaskChecker(task);
    }

    public TaskChecker create(CompareTask task) {
        return new TaskChecker(task);
    }

    public class TaskChecker {

        public TaskChecker(CompareTask task) {
            this.task = task;
        }

        private CompareTask task;

        private List<CompareDetail> details;

        public CompareTask getTask() {
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
            Assertions.assertEquals(TaskStatusEnum.SUCCESS, task.getTaskStatus());
            Assertions.assertNotNull(task.getCompareStatistic());
            return this;
        }

        public TaskChecker success(String expectStatistic) {
            Assertions.assertEquals(TaskStatusEnum.SUCCESS, task.getTaskStatus());
            Assertions.assertEquals(expectStatistic, task.getCompareStatistic());
            return this;
        }

        public TaskChecker fail(String expectStatistic) {
            Assertions.assertEquals(TaskStatusEnum.FAIL, task.getTaskStatus());
            Assertions.assertEquals(expectStatistic, task.getCompareStatistic());
            return this;
        }

        public TaskChecker compensate(TaskStatusEnum status, String expectStatistic) {
            Assertions.assertEquals(status, task.getTaskStatus());
            Assertions.assertEquals(expectStatistic, task.getCompareStatistic());
            return this;
        }

        public TaskChecker error(String errorCode, String errorMessage) {
            Assertions.assertEquals(TaskStatusEnum.ERROR, task.getTaskStatus());
            Assertions.assertNull(task.getCompareStatistic());
            Assertions.assertEquals(errorCode, task.getErrorCode());
            Assertions.assertEquals(errorMessage, task.getErrorMessage());
            return this;
        }

        public TaskChecker errorPrefix(String errorCode, String errorMessagePrefix) {
            Assertions.assertEquals(TaskStatusEnum.ERROR, task.getTaskStatus());
            Assertions.assertNull(task.getCompareStatistic());
            Assertions.assertEquals(errorCode, task.getErrorCode());
            Assertions.assertTrue(StringUtils.startsWith(task.getErrorMessage(), errorMessagePrefix));
            return this;
        }

        public TaskChecker retrySuccess(Long lastRetryTaskId) {
            Assertions.assertEquals(TaskStatusEnum.RETRY_SUCCESS, task.getTaskStatus());
            Assertions.assertEquals(lastRetryTaskId, task.getLastRetryTaskId());
            return this;
        }

        public TaskChecker manualConfirmed(String errorMessage, String operator) {
            Assertions.assertEquals(TaskStatusEnum.MANUAL_CONFIRMED, task.getTaskStatus());
            Assertions.assertEquals(errorMessage + " by " + operator, task.getErrorMessage());
            return this;
        }

        public TaskChecker errorPrefixMessage(String errorCode, String prefixMessage) {
            Assertions.assertEquals(TaskStatusEnum.ERROR, task.getTaskStatus());
            Assertions.assertNull(task.getCompareStatistic());
            Assertions.assertEquals(errorCode, task.getErrorCode());
            Assertions.assertTrue(StringUtils.startsWith(task.getErrorMessage(), prefixMessage));
            return this;
        }

        public TaskChecker detailCount(int expectSize) {
            if (details == null) {
                details = compareTestMapper.selectDetailByTaskId(task.getTaskId());
            }
            Assertions.assertEquals(expectSize, details.size());
            return this;
        }

        public CompareTaskDetailChecker detailChecker(int index) {
            return new CompareTaskDetailChecker(details.get(index));
        }

    }

}
