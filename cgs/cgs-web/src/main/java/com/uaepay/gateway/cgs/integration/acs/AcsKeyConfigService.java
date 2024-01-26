package com.uaepay.gateway.cgs.integration.acs;

import com.uaepay.acs.service.facade.key.domain.KeyConfig;

import java.util.List;

public interface AcsKeyConfigService {

    /**
     * 获取用来解密的密钥
     */
    List<KeyConfig> getDecryptKeys();

    String queryMerchantSetting(String merchantId, String paramKey);
}
