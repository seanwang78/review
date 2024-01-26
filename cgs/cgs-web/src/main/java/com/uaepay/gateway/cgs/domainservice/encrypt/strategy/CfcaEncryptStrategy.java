package com.uaepay.gateway.cgs.domainservice.encrypt.strategy;

import com.uaepay.gateway.cgs.integration.member.MaPayPasswordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CfcaEncryptStrategy extends AbstractSaltEncryptStrategy {

    public static final String STRATEGY_CODE = "CFCA";

    @Resource
    MaPayPasswordService maPayPasswordService;

    @Override
    public String getServiceCode() {
        return STRATEGY_CODE;
    }


    /**
     * 私钥解密
     *
     * @param fieldName
     *            字段名
     * @param original
     *            传输值
     * @return 明文
     */
    @Override
    protected String decrypt(String fieldName, String original, String salt) {
        return maPayPasswordService.decryptPayPassword(original, salt);
    }


    @Override
    protected String handlePlain(String fieldName, String plain, String salt) {
        return plain;
    }

}
