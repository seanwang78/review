package com.uaepay.pub.csc.service.facade;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.service.facade.request.*;

/**
 * 监控任务服务
 * 
 * @author zc
 */
public interface MonitorTaskFacade {

    /**
     * 申请人工检查
     *
     * @param request
     *            请求
     * @return 申请结果，申请成功返回taskId
     */
    ObjectQueryResponse<Long> applyManual(ManualMonitorRequest request);

    /**
     * 重试检查
     *
     * @param request
     *            请求
     * @return 申请结果，申请成功返回重试taskId
     */
    ObjectQueryResponse<Long> applyRetry(RetryMonitorRequest request);

    /**
     * 人工确认无问题
     * <li>只有当前状态为失败或异常的任务可以执行此操作</li>
     * 
     * @param request
     *            请求
     * @return 设置结果
     */
    CommonResponse manualConfirm(ManualConfirmMonitorRequest request);

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
