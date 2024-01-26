package com.uaepay.pub.csc.domainservice.monitor.schedule.impl;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.common.util.ErrorUtil;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;
import com.uaepay.pub.csc.domain.monitor.MonitorTaskBuilder;
import com.uaepay.pub.csc.domain.properties.ScheduleProperties;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorScheduleRepository;
import com.uaepay.pub.csc.domainservice.monitor.schedule.MonitorScheduleExecuteService;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskCallback;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskService;
import com.uaepay.pub.csc.service.facade.enums.MonitorTaskStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;

/**
 * @author zc
 */
@Service
public class MonitorScheduleExecuteServiceImpl implements MonitorScheduleExecuteService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MonitorTaskService monitorTaskService;

    @Autowired
    MonitorScheduleRepository monitorScheduleRepository;

    @Autowired
    ScheduleProperties scheduleProperties;

    @Override
    public void execute(MonitorSchedule schedule) throws TaskFullException {
        if (schedule.getScheduleStatus() != ScheduleStatusEnum.YES) {
            return;
        }
        logger.info("开始执行计划: schedule={}, checkMinutes={}", schedule.getScheduleId(), schedule.getCheckMinutes());
        if (schedule.getCurrentTaskId() != null) {
            if (schedule.getErrorCount() + 1 < scheduleProperties.getErrorToDisableCount()) {
                logger.error("任务异常重置，准备再次执行");
                monitorScheduleRepository.updateTaskErrorReset(schedule);
            } else {
                logger.error("超过异常次数限制，置为异常停用");
                monitorScheduleRepository.updateTaskErrorDisable(schedule);
                return;
            }
        }
        Date dataBeginTime = schedule.getCheckedTime();
        DateTime dataEndTime = new DateTime(schedule.getCheckedTime()).plusMinutes(schedule.getCheckMinutes());
        MonitorTask task = new MonitorTaskBuilder().schedule(schedule.getDefineId(), schedule.getScheduleId())
            .dataTime(dataBeginTime, dataEndTime.toDate()).build();
        ScheduleCallback callback = new ScheduleCallback(schedule);
        try {
            monitorTaskService.applyDaemonTask(task, callback);
            logger.info("任务提交成功: {}", task.getTaskId());
        } catch (Throwable e) {
            logger.error("任务异常", e);
            callback.onError(e);
        }
    }

    public class ScheduleCallback implements MonitorTaskCallback {

        public ScheduleCallback(MonitorSchedule schedule) {
            this.schedule = schedule;
        }

        private MonitorSchedule schedule;

        // @Override
        // public boolean checkStart(MonitorTask task) {
        // logger.info("更新计划最近任务");
        // schedule.setCurrentTaskId(task.getTaskId());
        // monitorScheduleRepository.updateTaskApplied(schedule, schedule.getCheckMinutes());
        // return true;
        // }

        @Override
        public void onSaveTask(MonitorTask task) {
            schedule.setCurrentTaskId(task.getTaskId());
            monitorScheduleRepository.updateTaskApplied(schedule, schedule.getCheckMinutes());
        }

        @Override
        public void onError(Throwable e) {
            boolean errorToDisable = ErrorUtil.isErrorNotRecoverable(e);
            processError(errorToDisable);
        }

        @Override
        public void onResult(MonitorTask task, MonitorResult result) {
            if (task.getTaskStatus() == MonitorTaskStatusEnum.ERROR) {
                processError(false);
            } else {
                schedule.setCheckedTime(task.getDataEndTime());
                DateTime nextTrigger = new DateTime(task.getDataEndTime()).plusMinutes(schedule.getCheckMinutes())
                    .plusMinutes(schedule.getDelayMinutes());
                if (nextTrigger.compareTo(DateTime.now()) >= 0) {
                    schedule.setNextTriggerTime(nextTrigger.toDate());
                    logger.info("更新计划进度, 下次执行时间: {}",
                        DateFormatUtils.ISO_DATETIME_FORMAT.format(schedule.getNextTriggerTime()));
                    monitorScheduleRepository.updateTaskNormal(schedule);
                } else {
                    schedule.setNextTriggerTime(DateTime.now().plusMinutes(schedule.getDelayMinutes()).toDate());
                    logger.info("更新计划进度，继续执行");
                    monitorScheduleRepository.updateTaskNormal(schedule);
                    execute(schedule);
                }
            }
        }

        public void processError(boolean errorToDisable) {
            if (errorToDisable) {
                logger.info("不可恢复异常，停用计划");
                monitorScheduleRepository.updateTaskErrorDisable(schedule);
                return;
            }
            if (schedule.getErrorCount() + 1 >= scheduleProperties.getErrorToDisableCount()) {
                logger.error("超过异常次数限制，停用计划");
                monitorScheduleRepository.updateTaskErrorDisable(schedule);
            } else {
                logger.error("任务异常，等待再次执行");
                monitorScheduleRepository.updateTaskErrorDelay(schedule, true,
                    scheduleProperties.getErrorDelayMinutes());
            }
        }
    }

}
