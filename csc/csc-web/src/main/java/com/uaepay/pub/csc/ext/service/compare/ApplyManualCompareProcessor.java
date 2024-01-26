package com.uaepay.pub.csc.ext.service.compare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.compare.CompareTaskBuilder;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskService;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;

/**
 * @author zc
 */
@Service
public class ApplyManualCompareProcessor
    extends AbstractProcessTemplate<ManualCompareRequest, ObjectQueryResponse<Long>> {

    @Autowired
    private CompareTaskService compareTaskService;

    @Override
    protected String getServiceName() {
        return "人工对账";
    }

    @Override
    protected ObjectQueryResponse<Long> createEmptyResponse() {
        return new ObjectQueryResponse<>();
    }

    @Override
    protected void validate(ManualCompareRequest request) {
        ParameterValidate.assertNotNull("dataBeginTime", request.getDataBeginTime());
        ParameterValidate.assertNotNull("dataEndTime", request.getDataEndTime());
        ParameterValidate.assertNotNull("defineId", request.getDefineId());
        ParameterValidate.assertNotBlank("operator", request.getOperator());
        ParameterValidate.assertTrue("dataBeginTime should be before dataEndTime",
            request.getDataBeginTime().compareTo(request.getDataEndTime()) < 0);
    }

    @Override
    protected void process(ManualCompareRequest request, ObjectQueryResponse<Long> response) {
        CompareTask compareTask = new CompareTaskBuilder().manual(request.getDefineId(), request.getOperator())
            .dataTime(request.getDataBeginTime(), request.getDataEndTime()).build();
        compareTaskService.applyTask(compareTask, null);
        response.success(compareTask.getTaskId());
    }

}
