package com.uaepay.gateway.cgs.app.service.integration;

import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.cms.facade.api.AppVersionFacade;
import com.uaepay.cms.facade.request.AppVersionQueryReq;
import com.uaepay.cms.facade.response.AppVersionResponse;
import com.uaepay.cmsii.facade.api.WhitelistFacade;
import com.uaepay.cmsii.facade.enums.FilterResult;
import com.uaepay.cmsii.facade.request.CheckRequest;
import com.uaepay.cmsii.facade.response.CheckResponse;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.uaepay.gateway.cgs.app.service.constants.CgsServiceLangCode.REMOTE_CALL_EXCEPTION;
import static com.uaepay.gateway.common.facade.enums.GatewayReturnCode.SYSTEM_ERROR;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/3/17
 * @since 0.1
 */
@Slf4j
@Component
public class CmsClient {
    @Reference
    private AppVersionFacade appVersionFacade;

    @Reference
    private WhitelistFacade whiteListFacade;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 查看是否是新灰度发布用户
     *
     * 为什么要放这么多参数？因为要组成缓存Key
     */
    public AppVersionResponse greaterVersion(String langType,
                                                     String platformType,
                                                     String versionCode,
                                                     String vectorCode,
                                                     String sdkVersion,
                                                     String hostAppChannel
    ) {
        AppVersionQueryReq req = new AppVersionQueryReq();
        req.setLangType(langType);
        req.setPlatformType(platformType);
        req.setVersionCode(versionCode);
        req.setVectorCode(vectorCode);
        req.setSdkVersion(sdkVersion);
        req.setHostAppChannel(hostAppChannel);
        return greaterVersion(req);
    }

    /**
     * 查询
     */
    public AppVersionResponse greaterVersion(AppVersionQueryReq queryReq) {

        try {
            log.info("[CmsClient -> greaterVersion -> request: {}]", queryReq);
            AppVersionResponse response = appVersionFacade.greaterCurrentVersion(queryReq);
            log.info("[CmsClient -> greaterVersion -> response: {}]", response);
            if (response.isSuccess()) {
                return response;
            }
        } catch (Exception e) {
            log.error("[CmsClient -> greaterVersion -> exception]", e);
            throw new GatewayFailException(SYSTEM_ERROR, REMOTE_CALL_EXCEPTION);
        }
        return null;
    }

    /**
     * 是否白名单
     *
     * @param memberId  用户Id
     * @param whitelistKey 白名单的Key
     * @return
     */
    public Boolean isWhitelist(String memberId,String whitelistKey) {

        if(StringUtils.isBlank(whitelistKey)) {
            return false;
        }

        CheckRequest request = new CheckRequest();

        request.setMemberId(memberId);
        request.setWhitelistKey(whitelistKey);
        request.setClientId(applicationName);
        try {
            log.info("cgs --> cms whiteListFacade.checkWasWhiteList request :{}",request);
            CheckResponse response = whiteListFacade.checkByKey(request);
            log.info("cgs <--  cms whiteListFacade.checkWasWhiteList response :{}",response);
            if(response != null && response.getApplyStatus() == ApplyStatusEnum.SUCCESS) {
                return ObjectUtils.defaultIfNull(response.getResult(), FilterResult.NO_PASS).isPass();
            }
        }catch (Throwable e) {
            log.error("[CmsClient -> whiteListFacade.checkWasWhiteList -> exception]", e);
            throw new GatewayFailException(SYSTEM_ERROR, REMOTE_CALL_EXCEPTION);
        }
        return false;
    }
}
