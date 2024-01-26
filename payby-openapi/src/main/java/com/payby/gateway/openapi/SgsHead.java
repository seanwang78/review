package com.payby.gateway.openapi;

import java.io.Serializable;

import lombok.Data;

@Data
public class SgsHead implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4287179071474838203L;

    public enum ApplyStatus {
        SUCCESS, FAIL, ERROR
    }

    /** request status */
    private String applyStatus;
    /** response code */
    private String code;
    /** msg */
    private String msg;
    /** sgs error trace code */
    private String traceCode;

    public boolean isSuccess() {
        return applyStatus != null && applyStatus.equalsIgnoreCase(ApplyStatus.SUCCESS.name()) && "0".equals(code);
    }
}
