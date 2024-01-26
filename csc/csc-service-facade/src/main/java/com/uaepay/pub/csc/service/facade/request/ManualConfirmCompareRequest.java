package com.uaepay.pub.csc.service.facade.request;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;

/**
 * 人工修复对账请求
 * 
 * @author zc
 */
public class ManualConfirmCompareRequest extends OperateRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 任务号，非空
     */
    private Long taskId;

    /**
     * 错误原因，非空
     */
    private String errorMessage;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
