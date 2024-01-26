package com.uaepay.pub.csc.service.facade.request;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;

/**
 * 重试检查请求
 * 
 * @author zc
 */
public class RetryMonitorRequest extends OperateRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 重试任务id
     */
    private long taskId;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

}
