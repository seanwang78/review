package com.uaepay.pub.csc.ext.service.monitor;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorRetryTaskService;
import com.uaepay.pub.csc.service.facade.request.RetryMonitorRequest;

/**
 * @author zc
 */
@Service
public class ApplyRetryMonitorProcessor
    extends AbstractProcessTemplate<RetryMonitorRequest, ObjectQueryResponse<Long>> {

    public ApplyRetryMonitorProcessor(MonitorTaskRepository monitorTaskRepository,
        MonitorRetryTaskService monitorRetryTaskService) {
        this.monitorTaskRepository = monitorTaskRepository;
        this.monitorRetryTaskService = monitorRetryTaskService;
    }

    private MonitorTaskRepository monitorTaskRepository;

    private MonitorRetryTaskService monitorRetryTaskService;

    @Override
    protected String getServiceName() {
        return "重试监控";
    }

    @Override
    protected ObjectQueryResponse<Long> createEmptyResponse() {
        return new ObjectQueryResponse<>();
    }

    @Override
    protected void validate(RetryMonitorRequest request) {
        ParameterValidate.assertNotBlank("operator", request.getOperator());
    }

    @Override
    protected void process(RetryMonitorRequest request, ObjectQueryResponse<Long> response) {
        MonitorTask origTask = monitorTaskRepository.getTask(request.getTaskId());
        if (origTask == null) {
            throw new BizCheckFailException("task not exist");
        }
        long retryTaskId = monitorRetryTaskService.retry(origTask, request.getOperator());
        response.success(retryTaskId);
    }

}
