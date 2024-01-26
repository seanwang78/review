package com.uaepay.pub.csc.service.facade.request;

import java.util.Date;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;

/**
 * 人工对账请求
 * 
 * @author zc
 */
public class ManualCompareRequest extends OperateRequest {

    private static final long serialVersionUID = 1L;

    private Long defineId;

    private Date dataBeginTime;

    private Date dataEndTime;

    public Long getDefineId() {
        return defineId;
    }

    public void setDefineId(Long defineId) {
        this.defineId = defineId;
    }

    public Date getDataBeginTime() {
        return dataBeginTime;
    }

    public void setDataBeginTime(Date dataBeginTime) {
        this.dataBeginTime = dataBeginTime;
    }

    public Date getDataEndTime() {
        return dataEndTime;
    }

    public void setDataEndTime(Date dataEndTime) {
        this.dataEndTime = dataEndTime;
    }
}
