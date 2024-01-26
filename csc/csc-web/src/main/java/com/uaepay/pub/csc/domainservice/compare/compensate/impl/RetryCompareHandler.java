package com.uaepay.pub.csc.domainservice.compare.compensate.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.compensation.event.domain.CompensationEvent;
import com.uaepay.basis.compensation.event.domain.ExecuteResult;
import com.uaepay.basis.compensation.event.service.CompensationEventHandler;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareTaskRepository;
import com.uaepay.pub.csc.domainservice.compare.task.CompareRetryTaskService;

/**
 * 补单之后重试对账处理器
 * 
 * @author zc
 */
@Service
public class RetryCompareHandler implements CompensationEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryCompareHandler.class);
    private static final String MAIN_TYPE = "RetryCompare";

    public static final CompensationEvent buildEvent(long taskId) {
        return CompensationEvent.newBuilder(taskId + "", MAIN_TYPE, "-").build();
    }

    @Autowired
    CompareTaskRepository compareTaskRepository;

    @Autowired
    CompareRetryTaskService compareRetryTaskService;

    @Override
    public String getServiceCode() {
        return MAIN_TYPE;
    }

    @Override
    public ExecuteResult handleEvent(CompensationEvent compensationEvent) {
        LOGGER.info("处理补单后重试对账事件: {}", compensationEvent);
        long taskId = Long.parseLong(compensationEvent.getEventKey());
        CompareTask task = compareTaskRepository.getTask(taskId);
        if (task == null) {
            return ExecuteResult.fail("任务不存在");
        }
        compareRetryTaskService.retry(task, "system", false, true);
        return ExecuteResult.success();
    }

}
