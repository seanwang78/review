package com.uaepay.gateway.cgs.integration.acs.impl;

import java.util.List;

import com.uaepay.acs.service.facade.legacy.SettingFacade;
import com.uaepay.acs.service.facade.legacy.request.MerchantSettingRequest;
import com.uaepay.acs.service.facade.legacy.response.MerchantSettingResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.uaepay.acs.service.facade.key.KeyConfigFacade;
import com.uaepay.acs.service.facade.key.domain.KeyConfig;
import com.uaepay.acs.service.facade.key.request.PartnerKeyQueryRequest;
import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.cgs.integration.BaseService;
import com.uaepay.gateway.cgs.integration.acs.AcsKeyConfigService;

@Service
public class AcsKeyConfigServiceImpl extends BaseService implements AcsKeyConfigService {

    public static final String PARTNER_ID = "APP";
    public static final String ALGORITHM = "RSA";
    public static final String CERT_TYPE = "PRIVATE";

    @Reference
    KeyConfigFacade keyConfigFacade;

    @Reference
    SettingFacade acsSettingFacade;

    @Override
    public List<KeyConfig> getDecryptKeys() {
        PartnerKeyQueryRequest request = new PartnerKeyQueryRequest();
        request.setClientId(clientId);
        request.setPartnerId(PARTNER_ID);
        request.setAlgorithm(ALGORITHM);
        request.setEnableFlag(YesNoEnum.YES);
        request.setCertType(CERT_TYPE);
        request.setGatewayType(GATEWAY_TYPE);
        // 1天之内即将生效
        request.setGmtEffectLessThan(new DateTime().plusDays(1).toDate());
        // 当前正在生效
        request.setGmtExpireGreaterThanOrEqualTo(new DateTime().toDate());
        logger.info("acs密钥查询请求: {}", request);
        ObjectQueryResponse<List<KeyConfig>> response = keyConfigFacade.queryPartnerKey(request);
        if (response.getApplyStatus() != ApplyStatusEnum.SUCCESS) {
            logger.info("acs密钥查询响应: {}", response);
            throw new ErrorException(response.getMessage());
        }
        logger.info("acs密钥查询响应: size = {}", CollectionUtils.size(response.getResult()));
        return response.getResult();
    }


    @Override
    @Cacheable(cacheNames="ACS_SETTING_KEY",key = "#paramKey + '-' + #merchantId")
    public String queryMerchantSetting(String merchantId, String paramKey) {
        MerchantSettingRequest request = new MerchantSettingRequest();
        request.setMerchantId(merchantId);
        request.setParamKey(paramKey);
        MerchantSettingResponse response = null;
        String value = "";
        try {
            logger.info("[cgs -> Acs.settingFacade.getMerchantSetting],request=[{}]", request);
            response = acsSettingFacade.getMerchantSetting(request);
            logger.info("[Acs.settingFacade.getMerchantSetting -> cgs],response=[{}]", response);

        } catch (Exception e) {
            logger.error("[Personal -> Acs.settingFacade.getMerchantSetting.] fail.", e);
            throw new ErrorException(CommonReturnCode.SYSTEM_ERROR);
        }
        if (response == null) {
            throw new ErrorException(CommonReturnCode.SYSTEM_ERROR);
        }
        if (!CollectionUtils.isEmpty(response.getMerchantSettingList())) {
            value = response.getMerchantSettingList().get(0).getParamValue();
        }
        return value;
    }
}
