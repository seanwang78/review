package com.uaepay.pub.csc.service.facade.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;
import com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum;

/**
 * 人工确认无问题请求
 * 
 * @author caoyongxing
 */
public class ManualConfirmLogMonitorRequest extends OperateRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 功能编码,CGS_LOG_MONITOR
     *
     * @see LogRuleFunctionCodeEnum
     */
    @NotBlank
    private String functionCode;

    /**
     * 日志分析id
     */
    @NotEmpty
    private List<Long> logStatIdList;

    public List<Long> getLogStatIdList() {
        return logStatIdList;
    }

    public void setLogStatIdList(List<Long> logStatIdList) {
        this.logStatIdList = logStatIdList;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }
}
