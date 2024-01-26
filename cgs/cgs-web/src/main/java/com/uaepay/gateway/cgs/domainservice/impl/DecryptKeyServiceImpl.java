package com.uaepay.gateway.cgs.domainservice.impl;

import com.uaepay.acs.service.facade.key.domain.KeyConfig;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.util.RsaUtil;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.gateway.cgs.constants.CgsCacheConstants;
import com.uaepay.gateway.cgs.domain.key.DecryptKeyConfig;
import com.uaepay.gateway.cgs.domainservice.DecryptKeyService;
import com.uaepay.gateway.cgs.integration.acs.AcsKeyConfigService;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DecryptKeyServiceImpl implements DecryptKeyService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public DecryptKeyServiceImpl(AcsKeyConfigService acsKeyConfigService, UesClientV2 uesClientV2) {
        this.acsKeyConfigService = acsKeyConfigService;
        this.uesClientV2 = uesClientV2;
    }

    private AcsKeyConfigService acsKeyConfigService;

    private UesClientV2 uesClientV2;

    @Override
    @Cacheable(value = CgsCacheConstants.DECRYPT_KEY, unless = "#result.size() == 0")
    public List<DecryptKeyConfig> getKeyConfigs() {
        List<KeyConfig> configs = acsKeyConfigService.getDecryptKeys();
        ArrayList<DecryptKeyConfig> result = new ArrayList<>();
        if (configs == null) {
            return result;
        }
        for (KeyConfig config : configs) {
            try {
                result.add(convert(config));
            } catch (InvalidKeySpecException e) {
                logger.error("密钥格式错误: " + config.getConfigId(), e);
            }
        }
        return result;
    }

    private DecryptKeyConfig convert(KeyConfig config) throws InvalidKeySpecException {
        DecryptKeyConfig result = new DecryptKeyConfig();
        result.setConfigId(config.getConfigId());
        result.setKeySize(2048);
        String privateKey = decryptUes(config);
        RsaUtil.getPrivateKey(privateKey);
        result.setPrivateKey(privateKey);
        result.setGmtEffect(config.getGmtEffect());
        result.setGmtExpired(config.getGmtExpired());
        return result;
    }

    private String decryptUes(KeyConfig config) {
        try {
            EncryptContextV2 contextV2 = new EncryptContextV2();
            contextV2.setTicket(config.getCert());
            boolean dataByTicket = uesClientV2.getDataByTicket(contextV2);
            if (!dataByTicket) {
                throw new FailException(CommonReturnCode.SYSTEM_ERROR, "ues解密失败");
            }
            return contextV2.getPlainData();
        } catch (Throwable e) {
            logger.error("解密私钥UES解密失败: " + config.getConfigId(), e);
            return config.getCert();
        }
    }

}
