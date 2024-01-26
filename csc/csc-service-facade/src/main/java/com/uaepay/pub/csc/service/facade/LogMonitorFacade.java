package com.uaepay.pub.csc.service.facade;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.service.facade.request.ManualConfirmLogMonitorRequest;
import com.uaepay.pub.csc.service.facade.request.RetryLogMonitorRequest;

/**
 * 网关日志监控
 * 
 * @author caoyongxing
 */
public interface LogMonitorFacade {

    /**
     * 重试检查
     *
     * @param request
     *            请求
     * @return 申请结果，申请成功返回重试taskId
     */
    CommonResponse applyRetry(RetryLogMonitorRequest request);

    /**
     * 人工确认无问题
     * <li>只有当前状态为失败的任务可以执行此操作</li>
     * 
     * @param request
     *            请求
     * @return 设置结果
     */
    CommonResponse manualConfirm(ManualConfirmLogMonitorRequest request);

}
