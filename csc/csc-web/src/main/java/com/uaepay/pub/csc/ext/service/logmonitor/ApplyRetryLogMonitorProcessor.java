package com.uaepay.pub.csc.ext.service.logmonitor;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.common.util.ValidatorUtil;
import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.domainservice.logmonitor.strategy.LogMonitorStrategyFactory;
import com.uaepay.pub.csc.service.facade.request.RetryLogMonitorRequest;

import lombok.AllArgsConstructor;

/**
 * @author caoyongxing
 */
@Service
@AllArgsConstructor
public class ApplyRetryLogMonitorProcessor extends AbstractProcessTemplate<RetryLogMonitorRequest, CommonResponse> {

    private LogMonitorStrategyFactory logMonitorStrategyFactory;

    @Override
    protected String getServiceName() {
        return "重试匹配异常日志";
    }

    @Override
    protected CommonResponse createEmptyResponse() {
        return new CommonResponse();
    }

    @Override
    protected void validate(RetryLogMonitorRequest request) {
        ValidatorUtil.validate(request);
        ParameterValidate.assertNotBlank("operator", request.getOperator());
        ParameterValidate.assertTrue("functionCode is not valid",
            logMonitorStrategyFactory.getByCode(request.getFunctionCode()) != null);
    }

    @Override
    protected void process(RetryLogMonitorRequest request, CommonResponse response) {
        CommonResponse result = logMonitorStrategyFactory.getByCode(request.getFunctionCode())
            .retry(request.getBeginTime(), request.getEndTime(), request.getOperator());
        response.from(result);
    }

}
