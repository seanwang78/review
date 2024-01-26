package com.uaepay.pub.csc.domainservice.compare.compensate.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.compensation.event.domain.CompensationEvent;
import com.uaepay.basis.compensation.event.domain.ExecuteResult;
import com.uaepay.basis.compensation.event.service.CompensationEventHandler;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.compensate.CompensateConfig;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareDefineRepository;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareTaskRepository;
import com.uaepay.pub.csc.ext.integration.CompensationClient;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;

/**
 * 补单处理器
 * 
 * @author zc
 */
@Service
public class CompensateHandler implements CompensationEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompensateHandler.class);
    private static final String MAIN_TYPE = "Compensate";

    public static final CompensationEvent buildEvent(long taskId) {
        return CompensationEvent.newBuilder(taskId + "", MAIN_TYPE, "-").build();
    }

    @Autowired
    CompareTaskRepository compareTaskRepository;

    @Autowired
    CompareDefineRepository compareDefineRepository;

    @Autowired
    CompensationClient compensationClient;

    @Override
    public String getServiceCode() {
        return MAIN_TYPE;
    }

    @Override
    public ExecuteResult handleEvent(CompensationEvent compensationEvent) {
        LOGGER.info("处理补单事件: {}", compensationEvent);
        long taskId = Long.parseLong(compensationEvent.getEventKey());
        CompareTask task = compareTaskRepository.getTask(taskId);
        if (task == null) {
            return ExecuteResult.fail("任务不存在");
        }
        // 任务重试成功，不执行补单
        if (task.getTaskStatus() == TaskStatusEnum.RETRY_SUCCESS) {
            return ExecuteResult.success();
        }
        // 任务不在补单中，状态异常失败
        if (task.getTaskStatus() != TaskStatusEnum.COMPENSATION_PROCESS) {
            return ExecuteResult.fail("任务状态异常: " + task.getTaskStatus());
        }

        // 查询补单配置
        CompareDefine define = compareDefineRepository.getByDefineId(task.getDefineId());
        if (StringUtils.isBlank(define.getCompensateConfig())) {
            return ExecuteResult.success();
        }
        CompensateConfig config = JSON.parseObject(define.getCompensateConfig(), CompensateConfig.class);
        if (StringUtils.isBlank(config.getAppCode()) || StringUtils.isBlank(config.getNotifyType())) {
            return ExecuteResult.error("补单配置异常");
        }

        // 查询需要补单的明细
        List<CompareDetail> details = compareTaskRepository.listWaitCompensateDetails(taskId);
        if (CollectionUtils.isEmpty(details)) {
            return ExecuteResult.success();
        }

        // 轮训通知
        compensate(details, config);

        // 更新任务状态 & 保存重试事件
        compareTaskRepository.updateCompensateFinished(task);

        return ExecuteResult.success();
    }

    private void compensate(List<CompareDetail> details, CompensateConfig config) {
        for (CompareDetail detail : details) {
            CommonResponse response = compensationClient.compensateSingle(detail, config);
            CompensateFlagEnum newStatus = CompensateFlagEnum.ERROR;
            if (response.getApplyStatus() == ApplyStatusEnum.SUCCESS) {
                newStatus = CompensateFlagEnum.SUCCESS;
            } else if (response.getApplyStatus() == ApplyStatusEnum.FAIL) {
                newStatus = CompensateFlagEnum.FAIL;
            }
            compareTaskRepository.updateDetailCompensate(detail, newStatus);
            if (newStatus == CompensateFlagEnum.ERROR) {
                throw new ErrorException("补单有异常，终止执行");
            }
        }
    }

}
