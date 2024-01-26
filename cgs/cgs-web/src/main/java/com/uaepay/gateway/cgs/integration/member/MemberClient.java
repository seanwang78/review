package com.uaepay.gateway.cgs.integration.member;

import com.uaepay.member.service.base.model.DecipherItem;
import com.uaepay.member.service.facade.IMemberFacade;
import com.uaepay.member.service.facade.IMemberKeyFacade;
import com.uaepay.member.service.request.DecipherInfoRequest;
import com.uaepay.member.service.request.MemberIntegratedRequest;
import com.uaepay.member.service.request.key.QueryMemberKeyRequest;
import com.uaepay.member.service.response.DecipherInfoResponse;
import com.uaepay.member.service.response.MemberIntegratedResponse;
import com.uaepay.member.service.response.key.QueryMemberKeyResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/4/6
 * @since 0.1
 */
@Slf4j
@Component
public class MemberClient {

    @Reference
    private IMemberFacade iMemberFacade;
    @Reference
    private IMemberKeyFacade memberKeyFacade;

    public MemberIntegratedResponse queryIntegrateMember(String partnerId, String identity) {
        MemberIntegratedRequest request = new MemberIntegratedRequest();
        request.setPartnerId(partnerId);
        request.setIdentity(identity);
        MemberIntegratedResponse response = null;
        try {
            if (log.isInfoEnabled()) {
                log.info("[MemberClient -> queryIntegrateMember -> request: {}]", request);
            }

            response = iMemberFacade.queryMemberIntegratedInfo(request);

            if (log.isInfoEnabled()) {
                log.info("[MemberClient -> queryIntegrateMember -> response: {}]", response);
            }
        } catch (Exception e) {
            log.error("[MemberClient -> queryIntegrateMember -> exception]", e);
        }
        if (response != null && response.isSuccess()) {
            return response;
        }
        return null;
    }


    public QueryMemberKeyResponse queryMemberKey(String memberId, String partnerId, String hostApp, String platform, String deviceId) {
        QueryMemberKeyRequest request = new QueryMemberKeyRequest();
        request.setMemberId(memberId);
        request.setPartnerId(partnerId);
        request.setHostApp(hostApp);
        request.setPlatform(platform);
        request.setDeviceId(deviceId);

        QueryMemberKeyResponse response = null;
        try {
            log.info("[MemberClient -> queryMemberKey -> request: {}]", request);
            response = memberKeyFacade.queryMemberKey(request);
            log.info("[MemberClient -> queryMemberKey -> response: {}]", response);
        } catch (Exception e) {
            log.error("[MemberClient -> queryMemberKey -> exception]", e);
        }
        if (response != null && response.isSuccess()) {
            return response;
        }
        return null;
    }



    public String queryMobileByMemberId(String memberId) {
        DecipherInfoRequest request = new DecipherInfoRequest();
        request.setMemberId(memberId);

        DecipherItem item = new DecipherItem();
        item.setDecipheredType(2);
        item.setQueryFlag(1);
        request.setColumnList(Arrays.asList(item));

        DecipherInfoResponse response;
        try {
            log.info("[cgs -> queryDecipherBy -> request: {}]", request);
            response = iMemberFacade.querytMemberDecipherInfo(request);
            log.info("[cgs -> queryDecipherBy -> response: {}]", response);
        } catch (Exception e) {
            log.error("[MemberClient -> querytMemberDecipherInfo -> exception]", e);
            return null;
        }
        if (response.isSuccess()) {
            if (CollectionUtils.isNotEmpty(response.getDecipheredResult())) {
                return response.getDecipheredResult().get(0).getPrimitiveValue();
            }
        }
        return null;
    }
}
