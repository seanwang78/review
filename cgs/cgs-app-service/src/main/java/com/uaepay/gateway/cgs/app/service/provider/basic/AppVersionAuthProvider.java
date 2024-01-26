package com.uaepay.gateway.cgs.app.service.provider.basic;

import com.uaepay.cms.facade.request.AppVersionQueryReq;
import com.uaepay.cms.facade.response.AppVersionResponse;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.service.domain.response.NewVersionResponse;
import com.uaepay.gateway.cgs.app.service.integration.CmsClient;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/3/17
 * @since 0.1
 */
@Service
public class AppVersionAuthProvider extends BaseAppVersionProvider {
    public static final String SERVICE_CODE = "/common/authCheck/appUpgrade";

    @Autowired
    private CmsClient cmsClient;

    @Override
    protected ApiType getApiType() {
        return ApiType.AUTHED;
    }

    @Override
    protected Class<Void> getReqBodyClazz() {
        return Void.class;
    }

    @Override
    protected ClientServiceResponse<NewVersionResponse> processService(ClientServiceRequest<Void> request) throws GatewayFailException {
        Header header = request.getHeader();
        AccessMember accessMember = request.getAccessMember();
        String langType = request.getLangType(),
                platformType = request.getPlatform().name().toUpperCase(),
                // 如果version-code没传 则取sdk版本号
                versionCode = header.getVersionCode(),
                // sdk版本
                sdkVersion = header.getSdkVersion(),
                // 载体APP
                vectorCode = header.getHostApp(),
                // App渠道
                hostAppChannel = header.getHostAppChannel(),
                // 会员Id
                memberId = accessMember.getMemberId();

        AppVersionQueryReq queryReq = new AppVersionQueryReq();
        queryReq.setLangType(langType);
        queryReq.setVersionCode(versionCode);
        queryReq.setVectorCode(vectorCode);
        queryReq.setSdkVersion(sdkVersion);
        queryReq.setVectorCode(vectorCode);
        queryReq.setHostAppChannel(hostAppChannel);
        queryReq.setMemberId(memberId);
        queryReq.setPlatformType(platformType);

        AppVersionResponse combo = cmsClient.greaterVersion(queryReq);
        return composeResult(combo);
    }

    @Override
    public String getServiceCode() {
        return SERVICE_CODE;
    }
}
