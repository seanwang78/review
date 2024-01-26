package com.uaepay.pub.csc.domainservice.compare.repository.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.compensation.event.domain.CompensationEvent;
import com.uaepay.basis.compensation.event.service.CompensationEventService;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareDetailMapper;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareTaskMapper;
import com.uaepay.pub.csc.domainservice.compare.compensate.impl.CompensateHandler;
import com.uaepay.pub.csc.domainservice.compare.compensate.impl.RetryCompareHandler;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareTaskRepository;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;

/**
 * @author zc
 */
@Repository
public class CompareTaskRepositoryImpl implements CompareTaskRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompareTaskRepositoryImpl.class);

    private static final int MAX_COMPARE_STATISTIC = 100;
    private static final int MAX_TASK_ERROR_CODE = 30;
    private static final int MAX_TASK_ERROR_MESSAGE = 250;
    private static final int MAX_RELATE_IDENTITY = 100;
    private static final int MAX_SRC_TARGET_DATE = 900;
    private static final int MAX_DETAIL_ERROR_MESSAGE = 128;

    @Autowired
    CompareTaskMapper compareTaskMapper;

    @Autowired
    CompareDetailMapper compareDetailMapper;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    CompensationEventService compensationEventService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public void saveTask(CompareTask compareTask) {
        compareTaskMapper.insertSelective(compareTask);
    }

    @Override
    public void updateSuccess(CompareTask compareTask) {
        compareTask
            .setCompareStatistic(StringUtils.abbreviate(compareTask.getCompareStatistic(), MAX_COMPARE_STATISTIC));
        int update = compareTaskMapper.updateSuccess(compareTask, TaskStatusEnum.SUCCESS);
        if (update != 1) {
            throw new ErrorException("更新对账成功失败");
        }
        compareTask.setTaskStatus(TaskStatusEnum.SUCCESS);
    }

    @Override
    public void updateError(CompareTask compareTask) {
        compareTask
            .setCompareStatistic(StringUtils.abbreviate(compareTask.getCompareStatistic(), MAX_COMPARE_STATISTIC));
        compareTask.setErrorCode(StringUtils.abbreviate(compareTask.getErrorCode(), MAX_TASK_ERROR_CODE));
        compareTask.setErrorMessage(StringUtils.abbreviate(compareTask.getErrorMessage(), MAX_TASK_ERROR_MESSAGE));
        int update = compareTaskMapper.updateError(compareTask, TaskStatusEnum.ERROR);
        if (update != 1) {
            throw new ErrorException("更新对账异常异常");
        }
        compareTask.setTaskStatus(TaskStatusEnum.ERROR);
    }

    @Override
    public void updateFail(CompareTask compareTask, List<CompareDetail> compareDetails, boolean compensate) {
        TaskStatusEnum newStatus = compensate ? TaskStatusEnum.COMPENSATION_PROCESS : TaskStatusEnum.FAIL;
        LOGGER.info("更新为对账失败");
        int update = compareTaskMapper.updateFail(compareTask, newStatus);
        if (update != 1) {
            throw new ErrorException("更新为对账失败异常");
        }
        compareTask.setTaskStatus(newStatus);
        LOGGER.info("保存对账明细");
        saveDetails(compareDetails);
        if (newStatus == TaskStatusEnum.COMPENSATION_PROCESS) {
            LOGGER.info("保存补单事件");
            CompensationEvent event = CompensateHandler.buildEvent(compareTask.getTaskId());
            compensationEventService.saveEvent(event);
            compensationEventService.asyncExecute(event);
        }
    }

    @Override
    public void updateLastRetryTask(CompareTask origTask, long retryTaskId) {
        int update = compareTaskMapper.updateLastRetryTask(origTask.getTaskId(), retryTaskId);
        if (update != 1) {
            throw new ErrorException("更新最近重试任务号异常");
        }
        origTask.setLastRetryTaskId(retryTaskId);
    }

    @Override
    public void updateRetrySuccess(CompareTask origTask) {
        int update = compareTaskMapper.updateRetrySuccess(origTask, TaskStatusEnum.RETRY_SUCCESS);
        if (update != 1) {
            throw new ErrorException("更新对账重试成功失败");
        }
        origTask.setTaskStatus(TaskStatusEnum.RETRY_SUCCESS);
    }

    @Override
    public void updateCompensateFinished(CompareTask task) {
        CompensationEvent event = RetryCompareHandler.buildEvent(task.getTaskId());
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                compensationEventService.saveEvent(event);
                updateStatus(task, TaskStatusEnum.RETRY_WAIT);
            }
        });
    }

    @Override
    public void updateStatus(CompareTask task, TaskStatusEnum newStatus) {
        int update = compareTaskMapper.updateStatus(task, newStatus);
        if (update != 1) {
            throw new ErrorException("更新任务状态失败");
        }
        task.setTaskStatus(newStatus);
    }

    @Override
    public void updateManualConfirmed(CompareTask task, String errorMessage) {
        task.setErrorMessage(errorMessage);
        int update = compareTaskMapper.updateManualConfirmed(task, TaskStatusEnum.MANUAL_CONFIRMED);
        if (update != 1) {
            throw new ErrorException("更新人工确认失败");
        }
        task.setTaskStatus(TaskStatusEnum.MANUAL_CONFIRMED);
    }

    @Override
    public int updateManualConfirmedBatch(List<Long> taskIds, String errorMessage) {
        return compareTaskMapper.updateManualConfirmedBatch(taskIds, errorMessage);
    }

    @Override
    public CompareTask getTask(long taskId) {
        return compareTaskMapper.selectByPrimaryKey(taskId);
    }

    @Override
    public List<CompareDetail> listWaitCompensateDetails(long taskId) {
        return compareDetailMapper.listWaitCompensateDetails(taskId);
    }

    @Override
    public void updateDetailCompensate(CompareDetail detail, CompensateFlagEnum newStatus) {
        int update =
            compareDetailMapper.updateDetailCompensate(detail.getDetailId(), detail.getCompensateFlag(), newStatus);
        if (update != 1) {
            throw new ErrorException("更新明细补单状态失败");
        }
        detail.setCompensateFlag(newStatus);
    }

    public void saveDetails(List<CompareDetail> details) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            for (CompareDetail detail : details) {
                detail.setRelateIdentity(StringUtils.abbreviate(detail.getRelateIdentity(), MAX_RELATE_IDENTITY));
                detail.setSrcData(StringUtils.abbreviate(detail.getSrcData(), MAX_SRC_TARGET_DATE));
                detail.setTargetData(StringUtils.abbreviate(detail.getTargetData(), MAX_SRC_TARGET_DATE));
                detail.setErrorMessage(StringUtils.abbreviate(detail.getErrorMessage(), MAX_DETAIL_ERROR_MESSAGE));
                session.insert("com.uaepay.pub.csc.core.dal.mapper.compare.CompareDetailMapper.insertSelective",
                    detail);
            }
            session.commit();
        }
    }

}
