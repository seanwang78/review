package com.uaepay.pub.csc.ext.service.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domain.monitor.MonitorTaskBuilder;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskService;
import com.uaepay.pub.csc.service.facade.request.ManualMonitorRequest;

/**
 * @author zc
 */
@Service
public class ApplyManualMonitorProcessor
    extends AbstractProcessTemplate<ManualMonitorRequest, ObjectQueryResponse<Long>> {

    @Autowired
    private MonitorTaskService monitorTaskService;

    @Override
    protected String getServiceName() {
        return "人工监控";
    }

    @Override
    protected ObjectQueryResponse<Long> createEmptyResponse() {
        return new ObjectQueryResponse<>();
    }

    @Override
    protected void validate(ManualMonitorRequest request) {
        ParameterValidate.assertNotNull("dataBeginTime", request.getDataBeginTime());
        ParameterValidate.assertNotNull("dataEndTime", request.getDataEndTime());
        ParameterValidate.assertNotNull("defineId", request.getDefineId());
        ParameterValidate.assertNotBlank("operator", request.getOperator());
        ParameterValidate.assertTrue("dataBeginTime should be before dataEndTime",
            request.getDataBeginTime().compareTo(request.getDataEndTime()) < 0);
    }

    @Override
    protected void process(ManualMonitorRequest request, ObjectQueryResponse<Long> response) {
        MonitorTask monitorTask = new MonitorTaskBuilder().manual(request.getDefineId(), request.getOperator())
            .dataTime(request.getDataBeginTime(), request.getDataEndTime()).build();
        monitorTaskService.applyTask(monitorTask, null);
        response.success(monitorTask.getTaskId());
    }

}
