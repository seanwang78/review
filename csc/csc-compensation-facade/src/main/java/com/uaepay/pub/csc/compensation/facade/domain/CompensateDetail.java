package com.uaepay.pub.csc.compensation.facade.domain;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 补单明细
 * 
 * @author zc
 */
public class CompensateDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号标志，非空
     */
    private String orderNo;

    /**
     * 比对状态，非空，L=少数据，M=不一致
     */
    private String compareStatus;

    /**
     * 通知状态，可空，例如：201, 401
     */
    private String notifyStatus;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCompareStatus() {
        return compareStatus;
    }

    public void setCompareStatus(String compareStatus) {
        this.compareStatus = compareStatus;
    }

    public String getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(String notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
