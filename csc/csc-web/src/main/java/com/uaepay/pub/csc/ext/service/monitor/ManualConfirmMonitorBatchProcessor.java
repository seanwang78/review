package com.uaepay.pub.csc.ext.service.monitor;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.common.util.ValidatorUtil;
import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmBatchRequest;

import lombok.AllArgsConstructor;

/**
 * @author zc
 */
@Service
@AllArgsConstructor
public class ManualConfirmMonitorBatchProcessor
    extends AbstractProcessTemplate<ManualConfirmBatchRequest, CommonResponse> {

    MonitorTaskRepository monitorTaskRepository;

    @Override
    protected String getServiceName() {
        return "批量人工确认监控";
    }

    @Override
    protected CommonResponse createEmptyResponse() {
        return new CommonResponse();
    }

    @Override
    protected void validate(ManualConfirmBatchRequest request) {
        ParameterValidate.assertNotBlank("operator", request.getOperator());
        ValidatorUtil.validate(request);
    }

    @Override
    protected void process(ManualConfirmBatchRequest request, CommonResponse response) {
        // pre process params
        Collections.sort(request.getTaskIds());
        String errorMessage = request.getErrorMessage() + " by " + request.getOperator();

        // execute
        int successCount = monitorTaskRepository.updateManualConfirmedBatch(request.getTaskIds(), errorMessage);
        int failCount = request.getTaskIds().size() - successCount;

        // response
        response.success(buildMessage(successCount, failCount));
    }

    private String buildMessage(int successCount, int failCount) {
        // Update success count: 10, update fail count: 1
        StringBuilder result = new StringBuilder();
        result.append("Update success count: ").append(successCount);
        if (failCount != 0) {
            result.append(", update fail count: ").append(failCount);
        }
        return result.toString();
    }

}
