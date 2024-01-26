package com.uaepay.gateway.cgs.app.facade.domain;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 响应头
 * 
 * @author zc
 */
public class ClientServiceResponseHead {

    public ClientServiceResponseHead() {}
    
    public ClientServiceResponseHead(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ClientServiceResponseHead(String code, String msg,Map<String,Object> extParm) {
        this.code = code;
        this.msg = msg;
        this.extParm = extParm;
    }

    public ClientServiceResponseHead(String code, String msg,Map<String,Object> extParm,Map<String,Object> data) {
        this.code = code;
        this.msg = msg;
        this.extParm = extParm;
        this.data = data;
    }

    /**
     * 网关返回码，如无必要不要自定义，尽量使用公共的返回码
     * @see com.uaepay.gateway.common.facade.enums.GatewayReturnCode
     */
    private String code;

    private String msg;
    
    /** 国际化跟踪码 */
    private String traceCode;

    /** code 非 200 情况下返回数据 */
    private Map<String,Object> data;

    @JsonIgnore
    private Map<String,Object> extParm;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    /**
	 * @return the traceCode
	 */
	public String getTraceCode() {
		return traceCode;
	}

	/**
	 * @param traceCode the traceCode to set
	 */
	public void setTraceCode(String traceCode) {
		this.traceCode = traceCode;
	}

	public Map<String, Object> getExtParm() {
        return extParm;
    }

    public void setExtParm(Map<String, Object> extParm) {
        this.extParm = extParm;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

}
