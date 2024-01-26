package com.uaepay.gateway.cgs.domainservice.encrypt.strategy;

import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategy;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Ues解密策略 .
 * <p>
 *
 * @author yusai
 * @date 2020-01-17 11:48.
 */
@Service
public class UesEncryptStrategy implements EncryptStrategy {

    public static final String STRATEGY_CODE = "UES";

    @Autowired
    private UesClientV2 uesClientV2;

    @Override
    public String getServiceCode() {
        return STRATEGY_CODE;
    }

    @Override
    public String convert(String fieldName, String original, ContextParameter emptyParameter) {
        return decrypt(original);
    }

    private String decrypt( String original) {
        EncryptContextV2 contextV2 = new EncryptContextV2();
        contextV2.setTicket(original);
        boolean dataByTicket = uesClientV2.getDataByTicket(contextV2);
        // 解密成功
        if (dataByTicket) {
            return contextV2.getPlainData();
        }
        return original;
    }
}
