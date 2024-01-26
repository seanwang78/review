package com.uaepay.gateway.cgs.domainservice.encrypt.strategy;

import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.gateway.cgs.common.AESUtil;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategy;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;

import static com.uaepay.gateway.cgs.domainservice.encrypt.strategy.AbstractSaltEncryptStrategy.PARAM_DECRYPT_FAILED;

/**
 * Ues解密策略 .
 * <p>
 *
 * @author yusai
 * @date 2020-01-17 11:48.
 */
@Service
public class AESEncryptStrategy implements EncryptStrategy {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String STRATEGY_CODE = "AES";

    @Autowired
    private UesClientV2 uesClientV2;

    @Value("${common.encrypt.aes.key}")
    private String aesKey;

    @Value("${common.encrypt.aes.iv}")
    private String aesIv;

    private AESUtil aesUtil;

    @Override
    public String getServiceCode() {
        return STRATEGY_CODE;
    }

    @PostConstruct
    public void init(){
        EncryptContextV2 contextV2 = new EncryptContextV2();
        contextV2.setTicket(aesKey);
        boolean dataByTicket = uesClientV2.getDataByTicket(contextV2);
        // 解密成功
        String aesKeyPlain = dataByTicket ? contextV2.getPlainData() : aesKey;

        contextV2 = new EncryptContextV2();
        contextV2.setTicket(aesIv);
        dataByTicket = uesClientV2.getDataByTicket(contextV2);
        String aesIvPlain = dataByTicket ? contextV2.getPlainData() : aesIv;

        aesUtil = new AESUtil().init(aesKeyPlain, aesIvPlain);

    }

    @Override
    public String convert(String fieldName, String original, ContextParameter emptyParameter) {
        try {
            return aesUtil.decrypt(original);
        } catch (Exception e){
            logger.info("解密失败", e);
            throw new InvalidParameterException(PARAM_DECRYPT_FAILED + fieldName);
        }
    }

}
