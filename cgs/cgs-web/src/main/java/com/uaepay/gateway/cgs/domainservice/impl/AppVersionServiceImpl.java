package com.uaepay.gateway.cgs.domainservice.impl;

import com.uaepay.cms.facade.request.AppVersionQueryReq;
import com.uaepay.cms.facade.response.AppVersionResponse;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.app.service.integration.CmsClient;
import com.uaepay.gateway.cgs.domain.AppUpgradeQuery;
import com.uaepay.gateway.cgs.domainservice.AppVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/3/24
 * @since 0.1
 */
@Slf4j
@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private CmsClient cmsClient;


    @Override
    public AppVersionResponse checkUpgrade(AppUpgradeQuery query) {
        if (this.supportPlatformType(query.getPlatformType())) {

            AppVersionQueryReq req = new AppVersionQueryReq();
            req.setLangType(query.getLangType());
            req.setPlatformType(query.getPlatformType().name().toUpperCase());
            req.setVersionCode(query.getVersionCode());
            req.setVectorCode(query.getVectorCode());
            req.setSdkVersion(query.getSdkVersion());
            req.setHostAppChannel(query.getHostAppChannel());
            return cmsClient.greaterVersion(req);
        }
        return null;
    }

    @Override
    public boolean supportPlatformType(PlatformType type) {
        return type == PlatformType.iOS || type == PlatformType.Android;
    }
}
