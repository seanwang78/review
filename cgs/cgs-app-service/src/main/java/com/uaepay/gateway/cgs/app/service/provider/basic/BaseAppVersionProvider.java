package com.uaepay.gateway.cgs.app.service.provider.basic;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.cms.facade.domain.AppVersionInfo;
import com.uaepay.cms.facade.response.AppVersionResponse;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.service.domain.UpgradeLevel;
import com.uaepay.gateway.cgs.app.service.domain.response.NewVersionResponse;
import com.uaepay.gateway.cgs.app.service.integration.CmsClient;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static com.uaepay.gateway.cgs.app.service.domain.UpgradeLevel.FORCE;
import static com.uaepay.gateway.cgs.app.service.domain.UpgradeLevel.TIP;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/5/29
 * @since 0.1
 */
public abstract class BaseAppVersionProvider extends AbstractClientServiceProvider<Void, NewVersionResponse> {

    @Autowired
    CmsClient cmsClient;

    @Override
    protected void processValidate(ClientServiceRequest<Void> request) throws GatewayFailException {
        Header header = request.getHeader();
        String langType = request.getLangType(),
                platformType = request.getPlatform().name().toUpperCase(),
                // 如果version-code没传 则取sdk版本号
                versionCode = header.getVersionCode(),
                // sdk版本号
                sdkVersion = header.getSdkVersion(),
                // 载体APP
                vectorCode = header.getHostApp();

        ParameterValidate.assertNotBlank("langType", langType);
        ParameterValidate.assertNotBlank("platformType", platformType);
        ParameterValidate.assertNotBlank("hostApp", vectorCode);
        if (StringUtils.isBlank(versionCode) && StringUtils.isBlank(sdkVersion)) {
            ParameterValidate.invalidParameter("versionCode or sdkVersion can't be null");
        }
    }

    protected ClientServiceResponse<NewVersionResponse> composeResult(AppVersionResponse combo){
        NewVersionResponse response = new NewVersionResponse();
        if (combo != null && combo.getGreaterVersion() != null) {
            AppVersionInfo greaterVersion = combo.getGreaterVersion();
            BeanUtils.copyProperties(greaterVersion, response);
            response.setUpgradeLevel(getUpgradeLevel(greaterVersion));
            response.setContents(greaterVersion.getContents());
        } else {
            response.setUpgradeLevel(UpgradeLevel.NONE);
        }
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, null, response);
    }

    protected Integer getUpgradeLevel(AppVersionInfo info) {
        if (Objects.equals(info.getIsForce(), 1)) {
            return FORCE;
        }
        return TIP;
    }
}
