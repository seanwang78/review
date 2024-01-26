package com.uaepay.pub.csc.domainservice.monitor.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;
import com.uaepay.pub.csc.domain.monitor.MonitorTaskBuilder;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorRetryTaskService;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskCallback;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskService;
import com.uaepay.pub.csc.service.facade.enums.MonitorTaskStatusEnum;

/**
 * @author zc
 */
@Service
public class MonitorRetryTaskServiceImpl implements MonitorRetryTaskService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MonitorTaskRepository monitorTaskRepository;

    @Autowired
    private MonitorTaskService monitorTaskService;

    @Override
    public long retry(MonitorTask origTask, String operator) {
        MonitorTask compareTask =
            new MonitorTaskBuilder().retryTask(origTask.getDefineId(), origTask.getTaskId(), operator)
                .dataTime(origTask.getDataBeginTime(), origTask.getDataEndTime()).build();
        RetryCallback callback = new RetryCallback(origTask);
        monitorTaskService.applyTask(compareTask, callback);
        return compareTask.getTaskId();
    }

    public class RetryCallback implements MonitorTaskCallback {

        public RetryCallback(MonitorTask origTask) {
            this.origTask = origTask;
        }

        MonitorTask origTask;

        // @Override
        // public boolean checkStart(MonitorTask task) {
        // if (isUpdateOrigTask()) {
        // logger.info("更新原任务的最后重试任务号");
        // monitorTaskRepository.updateLastRetryTask(origTask, task.getTaskId());
        // }
        // return true;
        // }

        @Override
        public void onSaveTask(MonitorTask task) {
            if (isUpdateOrigTask()) {
                monitorTaskRepository.updateLastRetryTask(origTask, task.getTaskId());
            }
        }

        @Override
        public void onError(Throwable e) {
            // do nothing
        }

        @Override
        public void onResult(MonitorTask task, MonitorResult result) {
            if (isUpdateOrigTask() && task.getTaskStatus() == MonitorTaskStatusEnum.SUCCESS) {
                logger.info("更新原任务状态为重试成功");
                monitorTaskRepository.updateRetrySuccess(origTask);
            }
        }

        private boolean isUpdateOrigTask() {
            return origTask.getTaskStatus() != MonitorTaskStatusEnum.SUCCESS
                && origTask.getTaskStatus() != MonitorTaskStatusEnum.RETRY_SUCCESS;
        }
    }

}
