package com.uaepay.gateway.cgs.domainservice.impl;

import com.uaepay.gateway.cgs.domainservice.MemberService;
import com.uaepay.gateway.cgs.integration.member.MemberClient;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.member.service.response.key.QueryMemberKeyResponse;
import com.uaepay.ues.UesClientV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.uaepay.gateway.cgs.app.service.common.CgsReturnCode.MEMBER_RSA_NOT_FOUND;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/7/25
 * @since 0.1
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberClient memberClient;
    @Autowired
    private UesClientV2 uesClientV2;


    @Override
    public String queryMemberRSAPublicKey(String memberId, String partnerId, String hostApp, String deviceId, String platform) {
        QueryMemberKeyResponse response = memberClient.queryMemberKey(memberId, partnerId, hostApp, platform, deviceId);
        if (response == null) {
            throw new GatewayFailException(MEMBER_RSA_NOT_FOUND);
        }
        return response.getCert();
    }
}
