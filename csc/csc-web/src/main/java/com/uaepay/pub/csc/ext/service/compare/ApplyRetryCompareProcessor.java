package com.uaepay.pub.csc.ext.service.compare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareTaskRepository;
import com.uaepay.pub.csc.domainservice.compare.task.CompareRetryTaskService;
import com.uaepay.pub.csc.service.facade.request.RetryCompareRequest;

/**
 * @author zc
 */
@Service
public class ApplyRetryCompareProcessor
    extends AbstractProcessTemplate<RetryCompareRequest, ObjectQueryResponse<Long>> {

    @Autowired
    protected CompareTaskRepository compareTaskRepository;

    @Autowired
    protected CompareRetryTaskService compareRetryTaskService;

    @Override
    protected String getServiceName() {
        return "重试对账";
    }

    @Override
    protected ObjectQueryResponse<Long> createEmptyResponse() {
        return new ObjectQueryResponse<>();
    }

    @Override
    protected void validate(RetryCompareRequest request) {
        ParameterValidate.assertNotBlank("operator", request.getOperator());
    }

    @Override
    protected void process(RetryCompareRequest request, ObjectQueryResponse<Long> response) {
        CompareTask origTask = compareTaskRepository.getTask(request.getTaskId());
        if (origTask == null) {
            throw new BizCheckFailException("task not exist");
        }
        long retryTaskId =
            compareRetryTaskService.retry(origTask, request.getOperator(), request.isCompensate(), false);
        response.success(retryTaskId);
    }

}
