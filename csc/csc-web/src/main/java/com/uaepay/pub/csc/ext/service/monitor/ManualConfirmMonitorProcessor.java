package com.uaepay.pub.csc.ext.service.monitor;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.service.facade.enums.MonitorTaskStatusEnum;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmMonitorRequest;

import lombok.AllArgsConstructor;

/**
 * @author zc
 */
@Service
@AllArgsConstructor
public class ManualConfirmMonitorProcessor
    extends AbstractProcessTemplate<ManualConfirmMonitorRequest, CommonResponse> {

    MonitorTaskRepository monitorTaskRepository;

    @Override
    protected String getServiceName() {
        return "人工确认监控";
    }

    @Override
    protected CommonResponse createEmptyResponse() {
        return new CommonResponse();
    }

    @Override
    protected void validate(ManualConfirmMonitorRequest request) {
        ParameterValidate.assertNotBlank("operator", request.getOperator());
        ParameterValidate.assertNotBlank("errorMessage", request.getErrorMessage());
    }

    @Override
    protected void process(ManualConfirmMonitorRequest request, CommonResponse response) {
        MonitorTask task = monitorTaskRepository.getTask(request.getTaskId());
        if (task == null) {
            throw new BizCheckFailException("task not exist");
        } else if (task.getTaskStatus() == MonitorTaskStatusEnum.MANUAL_CONFIRMED) {
            response.success("Duplicate request.");
            return;
        } else if (task.getTaskStatus() != MonitorTaskStatusEnum.FAIL
            && task.getTaskStatus() != MonitorTaskStatusEnum.ERROR) {
            throw new BizCheckFailException("task status error");
        }
        String errorMessage = request.getErrorMessage() + " by " + request.getOperator();
        monitorTaskRepository.updateManualConfirmed(task, errorMessage);
        response.success();
    }

}
