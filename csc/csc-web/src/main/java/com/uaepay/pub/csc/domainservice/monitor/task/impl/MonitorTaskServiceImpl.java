package com.uaepay.pub.csc.domainservice.monitor.task.impl;

import com.uaepay.pub.csc.domainservice.executor.CscDaemonTaskExecutor;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.core.IdGeneratorService;
import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domain.enums.SeqNameEnum;
import com.uaepay.pub.csc.domainservice.executor.CscNormalTaskExecutor;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorDefineRepository;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskCallback;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskRunnerFactory;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskService;

import lombok.AllArgsConstructor;

/**
 * @author zc
 */
@Service
@AllArgsConstructor
public class MonitorTaskServiceImpl implements MonitorTaskService {

    CscDaemonTaskExecutor cscDaemonTaskExecutor;

    CscNormalTaskExecutor cscNormalTaskExecutor;

    MonitorTaskRepository monitorTaskRepository;

    MonitorTaskRunnerFactory monitorTaskRunnerFactory;

    MonitorDefineRepository monitorDefineRepository;

    IdGeneratorService idGeneratorService;

    @Override
    public void applyDaemonTask(MonitorTask task, MonitorTaskCallback callback) {
        MonitorDefine define = getAndCheckDefine(task.getDefineId());
        task.setMonitorType(define.getMonitorType());
        task.setTaskId(idGeneratorService.getNextId(SeqNameEnum.SEQ_MONITOR_TASK));
        Runnable taskRunner = monitorTaskRunnerFactory.create(define, task, callback);
        cscDaemonTaskExecutor.getExecutor().execute(new RunnableWrapper(taskRunner));
    }

    @Override
    public void applyTask(MonitorTask task, MonitorTaskCallback callback) throws TaskFullException {
        MonitorDefine define = getAndCheckDefine(task.getDefineId());
        task.setMonitorType(define.getMonitorType());
        if (cscNormalTaskExecutor.isFull()) {
            throw new TaskFullException();
        }
        try {
            task.setTaskId(idGeneratorService.getNextId(SeqNameEnum.SEQ_MONITOR_TASK));
            Runnable taskRunner = monitorTaskRunnerFactory.create(define, task, callback);
            cscNormalTaskExecutor.getExecutor().execute(new RunnableWrapper(taskRunner));
        } catch (TaskRejectedException e) {
            throw new TaskFullException();
        }
    }

    private MonitorDefine getAndCheckDefine(long defineId) {
        MonitorDefine define = monitorDefineRepository.getByDefineId(defineId);
        if (define == null) {
            throw new FailException(CommonReturnCode.INVALID_PARAMETER, "define not found");
        }
        return define;
    }

}
