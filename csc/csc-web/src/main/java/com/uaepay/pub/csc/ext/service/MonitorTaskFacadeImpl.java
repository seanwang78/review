package com.uaepay.pub.csc.ext.service;

import org.apache.dubbo.config.annotation.Service;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.ext.service.monitor.ApplyManualMonitorProcessor;
import com.uaepay.pub.csc.ext.service.monitor.ApplyRetryMonitorProcessor;
import com.uaepay.pub.csc.ext.service.monitor.ManualConfirmMonitorBatchProcessor;
import com.uaepay.pub.csc.ext.service.monitor.ManualConfirmMonitorProcessor;
import com.uaepay.pub.csc.service.facade.MonitorTaskFacade;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmBatchRequest;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmMonitorRequest;
import com.uaepay.pub.csc.service.facade.request.ManualMonitorRequest;
import com.uaepay.pub.csc.service.facade.request.RetryMonitorRequest;

import lombok.AllArgsConstructor;

/**
 * 监控任务服务默认实现
 * 
 * @author zc
 */
@Service
@AllArgsConstructor
public class MonitorTaskFacadeImpl implements MonitorTaskFacade {

    ApplyManualMonitorProcessor applyManualMonitorProcessor;

    ApplyRetryMonitorProcessor applyRetryMonitorProcessor;

    ManualConfirmMonitorProcessor manualConfirmMonitorProcessor;

    ManualConfirmMonitorBatchProcessor manualConfirmMonitorBatchProcessor;

    @Override
    public ObjectQueryResponse<Long> applyManual(ManualMonitorRequest request) {
        return applyManualMonitorProcessor.process(request);
    }

    @Override
    public ObjectQueryResponse<Long> applyRetry(RetryMonitorRequest request) {
        return applyRetryMonitorProcessor.process(request);
    }

    @Override
    public CommonResponse manualConfirm(ManualConfirmMonitorRequest request) {
        return manualConfirmMonitorProcessor.process(request);
    }

    @Override
    public CommonResponse manualConfirmBatch(ManualConfirmBatchRequest request) {
        return manualConfirmMonitorBatchProcessor.process(request);
    }

}
