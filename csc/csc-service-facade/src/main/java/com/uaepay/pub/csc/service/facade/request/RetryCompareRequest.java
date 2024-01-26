package com.uaepay.pub.csc.service.facade.request;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;

/**
 * 重试对账请求
 * 
 * @author zc
 */
public class RetryCompareRequest extends OperateRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 重试任务id
     */
    private long taskId;

    /**
     * 是否补单
     */
    private boolean compensate;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public boolean isCompensate() {
        return compensate;
    }

    public void setCompensate(boolean compensate) {
        this.compensate = compensate;
    }
}
