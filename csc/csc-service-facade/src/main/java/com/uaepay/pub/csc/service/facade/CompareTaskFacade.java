package com.uaepay.pub.csc.service.facade;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.service.facade.request.ManualCompareRequest;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmBatchRequest;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmCompareRequest;
import com.uaepay.pub.csc.service.facade.request.RetryCompareRequest;

/**
 * 对账任务服务
 * 
 * @author zc
 */
public interface CompareTaskFacade {

    /**
     * 申请人工对账
     * 
     * @param request
     *            请求
     * @return 对账申请结果，申请成功返回taskId
     */
    ObjectQueryResponse<Long> applyManualCompare(ManualCompareRequest request);

    /**
     * 重试对账
     * 
     * @param request
     *            请求
     * @return 对账申请结果，申请成功返回重试对账taskId
     */
    ObjectQueryResponse<Long> applyRetryCompare(RetryCompareRequest request);

    /**
     * 人工确认无问题
     * <li>只有当前状态为失败或异常的任务可以执行此操作</li>
     *
     * @param request
     *            请求
     * @return 设置结果
     */
    CommonResponse manualConfirm(ManualConfirmCompareRequest request);

    /**
     * 批量人工确认
     * <li>只有当前状态为失败或异常的任务可以执行此操作</li>
     *
     * @param request
     *            请求
     * @return 申请结果，建议页面展示message
     */
    CommonResponse manualConfirmBatch(ManualConfirmBatchRequest request);

}
