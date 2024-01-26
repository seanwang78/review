package com.uaepay.pub.csc.datasource.facade.request;

import java.util.Date;

import com.uaepay.basis.beacon.service.facade.domain.request.AbstractRequest;

/**
 * 数据查询请求
 * 
 * @author zc
 */
public class QueryDataRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 对账数据开始时间，非空
     */
    private Date beginTime;

    /**
     * 对账数据结束时间，非空
     */
    private Date endTime;

    /**
     * 数据类型，可空，在对账定义中选配，用于支持同一个应用不同的数据
     */
    private String dataType;

    /**
     * 查询参数，可空，在对账定义中选配，业务自定义规则
     */
    private String queryParam;

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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }
}
