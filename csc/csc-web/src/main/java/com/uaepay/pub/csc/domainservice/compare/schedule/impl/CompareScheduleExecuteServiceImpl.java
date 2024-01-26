package com.uaepay.pub.csc.domainservice.compare.schedule.impl;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.common.util.ErrorUtil;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.compare.CompareResult;
import com.uaepay.pub.csc.domain.compare.CompareTaskBuilder;
import com.uaepay.pub.csc.domain.properties.ScheduleProperties;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareScheduleRepository;
import com.uaepay.pub.csc.domainservice.compare.schedule.CompareScheduleExecuteService;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskCallback;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskService;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;

/**
 * @author zc
 */
@Service
public class CompareScheduleExecuteServiceImpl implements CompareScheduleExecuteService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CompareTaskService compareTaskService;

    @Autowired
    CompareScheduleRepository compareScheduleRepository;

    @Autowired
    ScheduleProperties scheduleProperties;

    @Override
    public void execute(CompareSchedule schedule) throws TaskFullException {
        if (schedule.getScheduleStatus() != ScheduleStatusEnum.YES) {
            return;
        }
        logger.info("开始执行计划: schedule={}, checkMinutes={}", schedule.getScheduleId(), schedule.getCheckMinutes());
        if (schedule.getCurrentTaskId() != null) {
            if (schedule.getErrorCount() + 1 < scheduleProperties.getErrorToDisableCount()) {
                logger.error("任务异常重置，准备再次执行");
                compareScheduleRepository.updateTaskErrorReset(schedule);
            } else {
                logger.error("超过异常次数限制，置为异常停用");
                compareScheduleRepository.updateTaskErrorDisable(schedule);
                return;
            }
        }
        Date dataBeginTime = schedule.getCheckedTime();
        DateTime dataEndTime = new DateTime(schedule.getCheckedTime()).plusMinutes(schedule.getCheckMinutes());
        CompareTask compareTask = new CompareTaskBuilder().schedule(schedule.getScheduleId(), schedule.getDefineId())
            .dataTime(dataBeginTime, dataEndTime.toDate()).build();
        ScheduleCallback callback = new ScheduleCallback(schedule);
        try {
            compareTaskService.applyDaemonTask(compareTask, callback);
            logger.info("任务提交成功: {}", compareTask.getTaskId());
        } catch (Throwable e) {
            logger.error("任务异常", e);
            callback.onError(e);
        }
    }

    public class ScheduleCallback implements CompareTaskCallback {

        public ScheduleCallback(CompareSchedule schedule) {
            this.schedule = schedule;
        }

        private CompareSchedule schedule;

        // @Override
        // public boolean checkStart(CompareTask task) {
        // logger.info("更新计划最近任务");
        // schedule.setCurrentTaskId(task.getTaskId());
        // compareScheduleRepository.updateTaskApplied(schedule, schedule.getCheckMinutes());
        // return true;
        // }

        @Override
        public void onSaveTask(CompareTask task) {
            schedule.setCurrentTaskId(task.getTaskId());
            compareScheduleRepository.updateTaskApplied(schedule, schedule.getCheckMinutes());
        }

        @Override
        public boolean enableCompensate() {
            return true;
        }

        @Override
        public void onError(Throwable e) {
            boolean errorToDisable = ErrorUtil.isErrorNotRecoverable(e);
            processError(errorToDisable);
        }

        @Override
        public void onResult(CompareTask task, CompareResult result) {
            if (task.getTaskStatus() == TaskStatusEnum.ERROR) {
                processError(false);
            } else {
                schedule.setCheckedTime(task.getDataEndTime());
                DateTime nextTrigger = new DateTime(task.getDataEndTime()).plusMinutes(schedule.getCheckMinutes())
                    .plusMinutes(schedule.getDelayMinutes());
                if (nextTrigger.compareTo(DateTime.now()) >= 0) {
                    schedule.setNextTriggerTime(nextTrigger.toDate());
                    logger.info("更新计划进度, 下次执行时间: {}",
                        DateFormatUtils.ISO_DATETIME_FORMAT.format(schedule.getNextTriggerTime()));
                    compareScheduleRepository.updateTaskNormal(schedule);
                } else {
                    schedule.setNextTriggerTime(DateTime.now().plusMinutes(schedule.getDelayMinutes()).toDate());
                    logger.info("更新计划进度，继续执行");
                    compareScheduleRepository.updateTaskNormal(schedule);
                    execute(schedule);
                }
            }
        }

        public void processError(boolean errorToDisable) {
            if (errorToDisable) {
                logger.info("不可恢复异常，停用计划");
                compareScheduleRepository.updateTaskErrorDisable(schedule);
                return;
            }
            if (schedule.getErrorCount() + 1 >= scheduleProperties.getErrorToDisableCount()) {
                logger.error("超过异常次数限制，停用计划");
                compareScheduleRepository.updateTaskErrorDisable(schedule);
            } else {
                logger.error("任务异常，等待再次执行");
                compareScheduleRepository.updateTaskErrorDelay(schedule, true,
                    scheduleProperties.getErrorDelayMinutes());
            }
        }
    }

}
