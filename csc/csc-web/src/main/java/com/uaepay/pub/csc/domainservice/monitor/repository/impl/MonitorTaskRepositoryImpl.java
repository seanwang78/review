package com.uaepay.pub.csc.domainservice.monitor.repository.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTaskDetail;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorTaskMapper;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.service.facade.enums.MonitorTaskStatusEnum;

/**
 * @author zc
 */
@Service
public class MonitorTaskRepositoryImpl implements MonitorTaskRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorTaskRepositoryImpl.class);

    private static final int MAX_TASK_ERROR_MESSAGE = 250;
    private static final int MAX_DETAIL_CONTENT_MESSAGE = 1000;

    @Autowired
    MonitorTaskMapper monitorTaskMapper;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Override
    public void saveTask(MonitorTask task) {
        monitorTaskMapper.insertSelective(task);
    }

    @Override
    public MonitorTask getTask(long taskId) {
        return monitorTaskMapper.selectByPrimaryKey(taskId);
    }

    @Override
    public void updateSuccess(MonitorTask task, List<MonitorTaskDetail> details) {
        int update = monitorTaskMapper.updateSuccess(task, MonitorTaskStatusEnum.SUCCESS);
        if (update != 1) {
            throw new ErrorException("更新任务成功失败");
        }
        task.setTaskStatus(MonitorTaskStatusEnum.SUCCESS);
        if (CollectionUtils.isNotEmpty(details)) {
            LOGGER.info("保存任务明细");
            saveDetails(details);
        }
    }

    @Override
    public void updateFail(MonitorTask task, List<MonitorTaskDetail> details) {
        MonitorTaskStatusEnum newStatus = MonitorTaskStatusEnum.FAIL;
        LOGGER.info("更新任务为失败");
        int update = monitorTaskMapper.updateFail(task, newStatus);
        if (update != 1) {
            throw new ErrorException("更新失败异常");
        }
        task.setTaskStatus(newStatus);
        if (CollectionUtils.isNotEmpty(details)) {
            LOGGER.info("保存任务明细: count={}", details.size());
            saveDetails(details);
        }
    }

    @Override
    public void updateError(MonitorTask task) {
        task.setErrorMessage(StringUtils.abbreviate(task.getErrorMessage(), MAX_TASK_ERROR_MESSAGE));
        int update = monitorTaskMapper.updateError(task, MonitorTaskStatusEnum.ERROR);
        if (update != 1) {
            throw new ErrorException("更新任务异常异常");
        }
        task.setTaskStatus(MonitorTaskStatusEnum.ERROR);
    }

    @Override
    public void updateLastRetryTask(MonitorTask origTask, long retryTaskId) {
        int update = monitorTaskMapper.updateLastRetryTask(origTask.getTaskId(), retryTaskId);
        if (update != 1) {
            throw new ErrorException("更新最近重试任务号异常");
        }
        origTask.setLastRetryTaskId(retryTaskId);
    }

    @Override
    public void updateRetrySuccess(MonitorTask origTask) {
        int update = monitorTaskMapper.updateRetrySuccess(origTask, MonitorTaskStatusEnum.RETRY_SUCCESS);
        if (update != 1) {
            throw new ErrorException("更新对账重试成功失败");
        }
        origTask.setTaskStatus(MonitorTaskStatusEnum.RETRY_SUCCESS);
    }

    @Override
    public void updateManualConfirmed(MonitorTask task, String errorMessage) {
        task.setErrorMessage(errorMessage);
        int update = monitorTaskMapper.updateManualConfirmed(task, MonitorTaskStatusEnum.MANUAL_CONFIRMED);
        if (update != 1) {
            throw new ErrorException("更新人工确认失败");
        }
        task.setTaskStatus(MonitorTaskStatusEnum.MANUAL_CONFIRMED);
    }

    @Override
    public int updateManualConfirmedBatch(List<Long> taskIds, String errorMessage) {
        return monitorTaskMapper.updateManualConfirmedBatch(taskIds, errorMessage);
    }

    public void saveDetails(List<MonitorTaskDetail> details) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            for (MonitorTaskDetail detail : details) {
                detail.setDetailContent(StringUtils.abbreviate(detail.getDetailContent(), MAX_DETAIL_CONTENT_MESSAGE));
                session.insert("com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorTaskDetailMapper.insertSelective",
                    detail);
            }
            session.commit();
        }
    }

}
