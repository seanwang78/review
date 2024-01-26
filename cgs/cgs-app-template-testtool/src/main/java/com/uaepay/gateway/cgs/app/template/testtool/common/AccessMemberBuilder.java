package com.uaepay.gateway.cgs.app.template.testtool.common;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;

/**
 * AccessMember 构建构类 .
 * <p>
 *
 * @author yusai
 * @date 2020-06-14 12:15.
 */
public class AccessMemberBuilder {

    public static AccessMemberBuilder builder() {
        return new AccessMemberBuilder();
    }

    private AccessMember accessMember = new AccessMember();

    public AccessMemberBuilder memberId(String memberId) {
        accessMember.setMemberId(memberId);
        return this;
    }

    public AccessMemberBuilder mobile(String mobile) {
        accessMember.setMobile(mobile);
        return this;
    }

    public AccessMemberBuilder identity(String identity) {
        accessMember.setIdentity(identity);
        return this;
    }

    public AccessMemberBuilder partnerId(String partnerId) {
        accessMember.setPartnerId(partnerId);
        return this;
    }

    public AccessMemberBuilder hasRealName(YesNoEnum hasRealName) {
        accessMember.setHasPayPwd(hasRealName);
        return this;
    }

    public AccessMemberBuilder hasPayPwd(YesNoEnum hasPayPwd) {
        accessMember.setHasPayPwd(hasPayPwd);
        return this;
    }

    public AccessMember build() {
        return accessMember;
    }
}
