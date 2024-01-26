package com.uaepay.pub.csc.service.facade.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;

/**
 * 批量人工确认请求
 * 
 * @author zc
 */
public class ManualConfirmBatchRequest extends OperateRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 重试任务id列表
     */
    @NotNull
    @Size(min = 1, max = 200)
    private List<Long> taskIds;

    /**
     * 错误原因
     */
    @NotBlank
    private String errorMessage;

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
