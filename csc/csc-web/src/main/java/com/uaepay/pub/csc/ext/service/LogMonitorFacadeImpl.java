package com.uaepay.pub.csc.ext.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.ext.service.logmonitor.ApplyRetryLogMonitorProcessor;
import com.uaepay.pub.csc.ext.service.logmonitor.ManualConfirmLogMonitorProcessor;
import com.uaepay.pub.csc.service.facade.LogMonitorFacade;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmLogMonitorRequest;
import com.uaepay.pub.csc.service.facade.request.RetryLogMonitorRequest;

/**
 * @author cyx
 */
@Service
public class LogMonitorFacadeImpl implements LogMonitorFacade {

    @Autowired
    ApplyRetryLogMonitorProcessor applyRetryLogMonitorProcessor;

    @Autowired
    ManualConfirmLogMonitorProcessor manualConfirmLogMonitorProcessor;

    @Override
    public CommonResponse applyRetry(RetryLogMonitorRequest request) {
        return applyRetryLogMonitorProcessor.process(request);
    }

    @Override
    public CommonResponse manualConfirm(ManualConfirmLogMonitorRequest request) {
        return manualConfirmLogMonitorProcessor.process(request);
    }
}
