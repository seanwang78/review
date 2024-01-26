package com.uaepay.pub.csc.ext.service.compare;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.common.util.ValidatorUtil;
import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareTaskRepository;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmBatchRequest;

import lombok.AllArgsConstructor;

/**
 * @author zc
 */
@Service
@AllArgsConstructor
public class ManualConfirmCompareBatchProcessor
    extends AbstractProcessTemplate<ManualConfirmBatchRequest, CommonResponse> {

    CompareTaskRepository compareTaskRepository;

    @Override
    protected String getServiceName() {
        return "批量人工确认对账";
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
        int successCount = compareTaskRepository.updateManualConfirmedBatch(request.getTaskIds(), errorMessage);
        int failCount = request.getTaskIds().size() - successCount;

        // response
        response.success(buildMessage(successCount, failCount));
    }

    private String buildMessage(int successCount, int failCount) {
        StringBuilder result = new StringBuilder();
        result.append("Update success count: ").append(successCount);
        if (failCount != 0) {
            result.append(", update fail count: ").append(failCount);
        }
        return result.toString();
    }

}
