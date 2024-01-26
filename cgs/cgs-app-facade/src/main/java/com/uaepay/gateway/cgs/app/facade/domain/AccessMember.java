package com.uaepay.gateway.cgs.app.facade.domain;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class AccessMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memberId;

    /** 不再使用当前mobile字段 该字段永远为null */
    @Deprecated
    @ToStringExclude
    private String mobile;

    private String identity;

    private String partnerId;

    /**
     * 是否实名
     *
     * 当该值YES的时候可信任 当该值NO的时候 建议去ma重新查询是否实名
     */
    private YesNoEnum hasRealName;

    /**
     * 是否设置支付密码
     *
     * 当该值YES的时候可信任 当该值NO的时候 建议去ma重新查询是否设置支付密码
     */
    private YesNoEnum hasPayPwd;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public YesNoEnum getHasRealName() {
        return hasRealName;
    }

    public void setHasRealName(YesNoEnum hasRealName) {
        this.hasRealName = hasRealName;
    }

    public YesNoEnum getHasPayPwd() {
        return hasPayPwd;
    }

    public void setHasPayPwd(YesNoEnum hasPayPwd) {
        this.hasPayPwd = hasPayPwd;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

}
