package com.uaepay.pub.csc.domainservice.compare.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.compare.CompareResult;
import com.uaepay.pub.csc.domain.compare.CompareTaskBuilder;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareTaskRepository;
import com.uaepay.pub.csc.domainservice.compare.task.CompareRetryTaskService;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskCallback;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskService;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;

/**
 * @author zc
 */
@Service
public class CompareRetryTaskServiceImpl implements CompareRetryTaskService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CompareTaskRepository compareTaskRepository;

    @Autowired
    private CompareTaskService compareTaskService;

    @Override
    public long retry(CompareTask origTask, String operator, boolean compensate, boolean daemon) {
        CompareTask compareTask =
            new CompareTaskBuilder().retryTask(origTask.getDefineId(), origTask.getTaskId(), operator)
                .dataTime(origTask.getDataBeginTime(), origTask.getDataEndTime()).build();
        RetryCallback callback = new RetryCallback(origTask, compensate);
        if (daemon) {
            compareTaskService.applyDaemonTask(compareTask, callback);
        } else {
            compareTaskService.applyTask(compareTask, callback);
        }
        return compareTask.getTaskId();
    }

    public class RetryCallback implements CompareTaskCallback {

        public RetryCallback(CompareTask origTask, boolean compensate) {
            this.origTask = origTask;
            this.compensate = compensate;
        }

        CompareTask origTask;
        boolean compensate;

        // @Override
        // public boolean checkStart(CompareTask task) {
        // if (isUpdateOrigTask()) {
        // logger.info("更新原任务的最后重试任务号");
        // compareTaskRepository.updateLastRetryTask(origTask, task.getTaskId());
        // }
        // return true;
        // }

        @Override
        public void onSaveTask(CompareTask task) {
            if (isUpdateOrigTask()) {
                compareTaskRepository.updateLastRetryTask(origTask, task.getTaskId());
            }
        }

        @Override
        public boolean enableCompensate() {
            return compensate;
        }

        @Override
        public void onError(Throwable e) {
            // do nothing
        }

        @Override
        public void onResult(CompareTask task, CompareResult result) {
            if (isUpdateOrigTask() && task.getTaskStatus() == TaskStatusEnum.SUCCESS) {
                logger.info("更新原任务状态为重试成功");
                compareTaskRepository.updateRetrySuccess(origTask);
            }
        }

        private boolean isUpdateOrigTask() {
            return origTask.getTaskStatus() != TaskStatusEnum.SUCCESS
                && origTask.getTaskStatus() != TaskStatusEnum.RETRY_SUCCESS;
        }
    }

}
