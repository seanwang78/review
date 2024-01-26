package com.uaepay.pub.csc.service.facade.request;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;
import com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum;

/**
 * 重试检查请求
 * 
 * @author caoyongxing
 */
public class RetryLogMonitorRequest extends OperateRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 功能编码,CGS_LOG_MONITOR
     * 
     * @see LogRuleFunctionCodeEnum
     */
    @NotBlank
    private String functionCode;

    /**
     * 错误日志开始时间
     */
    @NotNull
    private Date beginTime;

    /**
     * 错误日志结束时间
     */
    @NotNull
    private Date endTime;

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
