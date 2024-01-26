package com.uaepay.pub.csc.ext.service.logmonitor;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.common.util.ValidatorUtil;
import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.domainservice.logmonitor.strategy.LogMonitorStrategyFactory;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmLogMonitorRequest;

import lombok.AllArgsConstructor;

/**
 * @author caoyongxing
 */
@Service
@AllArgsConstructor
public class ManualConfirmLogMonitorProcessor
    extends AbstractProcessTemplate<ManualConfirmLogMonitorRequest, CommonResponse> {

    private LogMonitorStrategyFactory logMonitorStrategyFactory;

    @Override
    protected String getServiceName() {
        return "人工确认异常日志";
    }

    @Override
    protected CommonResponse createEmptyResponse() {
        return new CommonResponse();
    }

    @Override
    protected void validate(ManualConfirmLogMonitorRequest request) {
        ValidatorUtil.validate(request);
        ParameterValidate.assertNotBlank("operator", request.getOperator());
        ParameterValidate.assertTrue("functionCode is not valid",
            logMonitorStrategyFactory.getByCode(request.getFunctionCode()) != null);

    }

    @Override
    protected void process(ManualConfirmLogMonitorRequest request, CommonResponse response) {
        CommonResponse result = logMonitorStrategyFactory.getByCode(request.getFunctionCode())
            .manualConfirm(request.getLogStatIdList(), request.getOperator());
        response.from(result);
    }

}
