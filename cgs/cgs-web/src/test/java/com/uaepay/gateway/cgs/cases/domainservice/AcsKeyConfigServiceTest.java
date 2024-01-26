package com.uaepay.gateway.cgs.cases.domainservice;

import com.uaepay.acs.service.facade.key.domain.KeyConfig;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.util.RsaUtil;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.gateway.cgs.domain.key.DecryptKeyConfig;
import com.uaepay.gateway.cgs.integration.acs.AcsKeyConfigService;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/7/28
 * @since 0.1
 */
public class AcsKeyConfigServiceTest extends ApplicationTestBase {

    @Autowired
    private UesClientV2 uesClientV2;
    @Autowired
    private AcsKeyConfigService acsKeyConfigService;
    @Test
    public void test() {
        List<KeyConfig> configs = acsKeyConfigService.getDecryptKeys();
        System.out.println(configs);
        ArrayList<DecryptKeyConfig> result = new ArrayList<>();
        if (configs == null) {
            return ;
        }
        for (KeyConfig config : configs) {
            try {
                result.add(convert(config));
            } catch (InvalidKeySpecException e) {
                logger.error("密钥格式错误: " + config.getConfigId(), e);
            }
        }
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
            if (dataByTicket) {
                throw new FailException(CommonReturnCode.SYSTEM_ERROR, "ues解密失败");
            }
            return contextV2.getPlainData();
        } catch (Throwable e) {
            logger.error("解密私钥UES解密失败: " + config.getConfigId(), e);
            return config.getCert();
        }
    }

}
