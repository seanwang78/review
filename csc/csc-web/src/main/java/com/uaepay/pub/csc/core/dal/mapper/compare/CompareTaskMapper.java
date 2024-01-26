package com.uaepay.pub.csc.core.dal.mapper.compare;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;

public interface CompareTaskMapper {

    int insertSelective(CompareTask record);

    CompareTask selectByPrimaryKey(Long taskId);

    int updateError(@Param("task") CompareTask compareTask, @Param("newStatus") TaskStatusEnum newStatus);

    int updateFail(@Param("task") CompareTask compareTask, @Param("newStatus") TaskStatusEnum newStatus);

    int updateSuccess(@Param("task") CompareTask compareTask, @Param("newStatus") TaskStatusEnum newStatus);

    int updateLastRetryTask(@Param("origTaskId") long origTaskId, @Param("retryTaskId") long retryTaskId);

    int updateRetrySuccess(@Param("task") CompareTask compareTask, @Param("newStatus") TaskStatusEnum newStatus);

    int updateStatus(@Param("task") CompareTask compareTask, @Param("newStatus") TaskStatusEnum newStatus);

    int updateManualConfirmed(@Param("task") CompareTask task, @Param("newStatus") TaskStatusEnum newStatus);

    int updateManualConfirmedBatch(@Param("taskIds") List<Long> taskIds, @Param("errorMessage") String errorMessage);

}