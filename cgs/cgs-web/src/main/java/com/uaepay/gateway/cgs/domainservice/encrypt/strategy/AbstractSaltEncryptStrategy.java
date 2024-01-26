package com.uaepay.gateway.cgs.domainservice.encrypt.strategy;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.basis.beacon.common.util.RsaUtil;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.domain.key.DecryptKeyConfig;
import com.uaepay.gateway.cgs.domainservice.DecryptKeyService;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategy;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import com.uaepay.ues.ctx.params.Temporarily;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.List;

import static com.uaepay.gateway.cgs.app.service.common.CgsReturnCode.SALT_EXPIRED;

/**
 * 解密加密策略抽象，功能如下
 * <li>用私钥解密，剔除salt</li>
 * <li>用ues加密为临时token</li>
 */
public abstract class AbstractSaltEncryptStrategy implements EncryptStrategy {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // protected static final String SALT_EXPIRED = "${gateway.saltExpired}";
    protected static final String PARAM_DECRYPT_FAILED = "${gateway.paramDecryptFailed}: ";
    protected static final String PARAM_PLAIN_WITHOUT_SALT = "${gateway.paramPlainWithoutSalt}: ";

    @Autowired
    private DecryptKeyService decryptKeyService;

    @Autowired
    private UesClientV2 uesClient;

    @Value("${common.encrypt.ttl}")
    private Duration encryptTtl;

    @Override
    public String convert(String fieldName, String original, ContextParameter saltParameter) {
        String salt = saltParameter.getSalt();

        validateSaltExist(salt);

        String plain = decrypt(fieldName, original, salt);

        plain = handlePlain(fieldName, plain, salt);

        return encryptUesTemp(plain);
    }

    /**
     * 验证盐令牌
     */
    protected void validateSaltExist(String salt) {
        if (StringUtils.isBlank(salt)) {
            throw new GatewayFailException(SALT_EXPIRED);
        }
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
    protected String decrypt(String fieldName, String original, String salt) {
        List<DecryptKeyConfig> configs = decryptKeyService.getKeyConfigs();
        for (DecryptKeyConfig config : configs) {
            if (!config.isEffect()) {
                continue;
            }
            try {
                return RsaUtil.decrypt(original, GatewayConstants.DEFAULT_CHARSET, config.getPrivateKey(),
                    config.getKeySize());
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        throw new InvalidParameterException(PARAM_DECRYPT_FAILED + fieldName);
    }

    /**
     * 处理原始明文，默认明文后缀就是盐
     *
     * @return 去掉盐之后的明文
     */
    protected String handlePlain(String fieldName, String plain, String salt) {
        if (!StringUtils.endsWith(plain, salt)) {
            // logger.info("[handlePlain -> fieldName: {}, plain salt: {}, salt: {} ]", fieldName, MaskUtil.getFiledMask()plain.substring(), salt);
            throw new InvalidParameterException(PARAM_PLAIN_WITHOUT_SALT + fieldName);
        }
        return plain.substring(0, plain.length() - salt.length());
    }

    /**
     * 加密为ues临时token
     * 
     * @param plain
     *            明文
     * @return ues令牌
     */
    protected String encryptUesTemp(String plain) {
        EncryptContextV2 context = new EncryptContextV2();
        context.setPlainData(plain);
        Temporarily temporarily = new Temporarily();
        temporarily.setTimeout((int)(encryptTtl.toMillis() / 1000));
        // 临时加密无需RSA，提升效率
        temporarily.setNeedRsa(false);
        context.inTemporarily(temporarily);
        context.setDataType("cgs_common_data");
        try {
            boolean success = uesClient.saveData(context);
            if (!success) {
                throw new ErrorException("加密异常: " + context.getResultCode() + ", " + context.getResultMessage());
            }
        } catch (InvocationTargetException e) {
            throw new ErrorException("加密异常: 调用异常");
        } catch (Exception e) {
            logger.error("加密数据转ues:",e);
            throw new ErrorException("加密异常: 调用异常");
        }
        return context.getTicket();
    }

}
