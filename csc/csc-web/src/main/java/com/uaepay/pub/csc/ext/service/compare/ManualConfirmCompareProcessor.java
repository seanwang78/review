package com.uaepay.pub.csc.ext.service.compare;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareTaskRepository;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmCompareRequest;

import lombok.AllArgsConstructor;

/**
 * @author zc
 */
@Service
@AllArgsConstructor
public class ManualConfirmCompareProcessor
    extends AbstractProcessTemplate<ManualConfirmCompareRequest, CommonResponse> {

    CompareTaskRepository compareTaskRepository;

    @Override
    protected String getServiceName() {
        return "人工确认对账";
    }

    @Override
    protected CommonResponse createEmptyResponse() {
        return new CommonResponse();
    }

    @Override
    protected void validate(ManualConfirmCompareRequest request) {
        ParameterValidate.assertNotBlank("operator", request.getOperator());
        ParameterValidate.assertNotBlank("errorMessage", request.getErrorMessage());
    }

    @Override
    protected void process(ManualConfirmCompareRequest request, CommonResponse response) {
        CompareTask task = compareTaskRepository.getTask(request.getTaskId());
        if (task == null) {
            throw new BizCheckFailException("task not exist");
        } else if (task.getTaskStatus() == TaskStatusEnum.MANUAL_CONFIRMED) {
            response.success("Duplicate request.");
            return;
        } else if (!task.getTaskStatus().isManualConfirmable()) {
            throw new BizCheckFailException("task status error");
        }
        String errorMessage = request.getErrorMessage() + " by " + request.getOperator();
        compareTaskRepository.updateManualConfirmed(task, errorMessage);
        response.success();
    }

}
