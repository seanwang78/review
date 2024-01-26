package com.uaepay.gateway.cgs.app.service.provider.basic;

import com.uaepay.cms.facade.response.AppVersionResponse;
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
public class AppVersionProvider extends BaseAppVersionProvider {
    public static final String SERVICE_CODE = "/common/check/appUpgrade";

    @Autowired
    private CmsClient cmsClient;

    @Override
    protected ApiType getApiType() {
        return ApiType.UNAUTHED;
    }

    @Override
    protected Class<Void> getReqBodyClazz() {
        return Void.class;
    }

    @Override
    protected ClientServiceResponse<NewVersionResponse> processService(ClientServiceRequest<Void> request) throws GatewayFailException {
        Header header = request.getHeader();
        String langType = request.getLangType(),
                platformType = request.getPlatform().name().toUpperCase(),
                // 如果version-code没传 则取sdk版本号
                versionCode = header.getVersionCode(),
                // skd版本
                sdkVersion = header.getSdkVersion(),
                // 主机App渠道
                hostAppChannel = header.getHostAppChannel(),
                // 载体APP
                vectorCode = header.getHostApp();

        AppVersionResponse combo = cmsClient.greaterVersion(langType, platformType, versionCode, vectorCode, sdkVersion, hostAppChannel);
        return composeResult(combo);
    }

    @Override
    public String getServiceCode() {
        return SERVICE_CODE;
    }
}
