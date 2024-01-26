package com.uaepay.pub.csc.domainservice.compare.task.impl;

import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.IdGeneratorService;
import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.enums.SeqNameEnum;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskCallback;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskRunnerFactory;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskService;
import com.uaepay.pub.csc.domainservice.executor.CscDaemonTaskExecutor;
import com.uaepay.pub.csc.domainservice.executor.CscNormalTaskExecutor;

/**
 * @author zc
 */
@Service
public class CompareTaskServiceImpl implements CompareTaskService {

    @Autowired
    CompareTaskRunnerFactory compareTaskRunnerFactory;

    @Autowired
    CscDaemonTaskExecutor cscDaemonTaskExecutor;

    @Autowired
    CscNormalTaskExecutor cscNormalTaskExecutor;

    @Autowired
    IdGeneratorService idGeneratorService;

    @Override
    public void applyDaemonTask(CompareTask task, CompareTaskCallback callback) {
        task.setTaskId(idGeneratorService.getNextId(SeqNameEnum.SEQ_COMPARE_TASK));
        Runnable taskRunner = compareTaskRunnerFactory.create(task, callback);
        cscDaemonTaskExecutor.getExecutor().execute(new RunnableWrapper(taskRunner));
    }

    @Override
    public void applyTask(CompareTask task, CompareTaskCallback callback) throws TaskFullException {
        if (cscNormalTaskExecutor.isFull()) {
            throw new TaskFullException();
        }
        try {
            task.setTaskId(idGeneratorService.getNextId(SeqNameEnum.SEQ_COMPARE_TASK));
            Runnable taskRunner = compareTaskRunnerFactory.create(task, callback);
            cscNormalTaskExecutor.getExecutor().execute(new RunnableWrapper(taskRunner));
        } catch (TaskRejectedException e) {
            throw new TaskFullException();
        }
    }

}
