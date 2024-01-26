package com.uaepay.gateway.cgs.app.service.integration;

import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.kyc.base.response.ServiceResponse;
import com.uaepay.kyc.facade.TokenFacade;
import com.uaepay.kyc.request.token.ValidateAccessTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KycServiceClient {

    @Reference
    TokenFacade tokenFacade;

    @Value("${spring.application.name}")
    private String applicationName;


    public boolean validateAccessToken(String accessToken, String identity, String accountId, String deviceId, String appId) {
        try {
            ValidateAccessTokenRequest request = new ValidateAccessTokenRequest();
            request.setIdentity(identity);
            request.setAccountId(accountId);
            request.setDeviceId(deviceId);
            request.setAppId(appId);
            request.setAccessToken(accessToken);
            request.setClientId(applicationName);

            log.info("cgs -> kyc-service validateAccessToken request:{}",request);
            ServiceResponse response = tokenFacade.validateAccessToken(request);
            log.info("cgs -> kyc-service validateAccessToken response:{}",response);

            if(response != null && response.getApplyStatus() == ApplyStatusEnum.SUCCESS) {
                return true;
            }

        }catch (Throwable e) {
            log.error("调用kyc-service validateAccessToken 异常:",e);
            return false;
        }
        return false;
    }
}
