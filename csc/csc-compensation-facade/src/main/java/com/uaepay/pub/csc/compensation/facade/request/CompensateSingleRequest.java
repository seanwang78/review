package com.uaepay.pub.csc.compensation.facade.request;

import com.uaepay.basis.beacon.service.facade.domain.request.AbstractRequest;
import com.uaepay.pub.csc.compensation.facade.domain.CompensateDetail;

/**
 * 补单请求
 * 
 * @author zc
 */
public class CompensateSingleRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 对账任务id
     */
    private long taskId;

    /**
     * 通知类型，非空，例如：TRADE_NOTIFY
     */
    private String notifyType;

    /**
     * 补单明细，非空
     */
    private CompensateDetail detail;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public CompensateDetail getDetail() {
        return detail;
    }

    public void setDetail(CompensateDetail detail) {
        this.detail = detail;
    }
}
