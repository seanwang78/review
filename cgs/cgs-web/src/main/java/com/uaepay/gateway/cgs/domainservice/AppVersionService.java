package com.uaepay.gateway.cgs.domainservice;

import com.uaepay.cms.facade.response.AppVersionResponse;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.domain.AppUpgradeQuery;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/3/24
 * @since 0.1
 */
public interface AppVersionService {

    AppVersionResponse checkUpgrade(AppUpgradeQuery query);

    /**
     * 升级接口支持的平台类型
     */
    boolean supportPlatformType(PlatformType type);
}
