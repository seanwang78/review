package com.uaepay.pub.csc.ext.service;

import org.apache.dubbo.config.annotation.Service;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.ext.service.compare.ApplyManualCompareProcessor;
import com.uaepay.pub.csc.ext.service.compare.ApplyRetryCompareProcessor;
import com.uaepay.pub.csc.ext.service.compare.ManualConfirmCompareBatchProcessor;
import com.uaepay.pub.csc.ext.service.compare.ManualConfirmCompareProcessor;
import com.uaepay.pub.csc.service.facade.CompareTaskFacade;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmBatchRequest;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmCompareRequest;
import com.uaepay.pub.csc.service.facade.request.RetryCompareRequest;

import lombok.AllArgsConstructor;

/**
 * @author zc
 */
@Service
@AllArgsConstructor
public class CompareTaskFacadeImpl implements CompareTaskFacade {

    ApplyManualCompareProcessor applyManualCompareProcessor;

    ApplyRetryCompareProcessor applyRetryCompareProcessor;

    ManualConfirmCompareProcessor manualConfirmCompareProcessor;

    ManualConfirmCompareBatchProcessor manualConfirmCompareBatchProcessor;

    @Override
    public ObjectQueryResponse<Long> applyManualCompare(ManualCompareRequest request) {
        return applyManualCompareProcessor.process(request);
    }

    @Override
    public ObjectQueryResponse<Long> applyRetryCompare(RetryCompareRequest request) {
        return applyRetryCompareProcessor.process(request);
    }

    @Override
    public CommonResponse manualConfirm(ManualConfirmCompareRequest request) {
        return manualConfirmCompareProcessor.process(request);
    }

    @Override
    public CommonResponse manualConfirmBatch(ManualConfirmBatchRequest request) {
        return manualConfirmCompareBatchProcessor.process(request);
    }

}
