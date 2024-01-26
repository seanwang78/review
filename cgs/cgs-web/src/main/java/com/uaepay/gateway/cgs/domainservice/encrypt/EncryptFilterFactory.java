package com.uaepay.gateway.cgs.domainservice.encrypt;

/**
 * 加密过滤器工厂
 * 
 * @author zc
 */
public interface EncryptFilterFactory {

    /**
     * API配置的encrypt配置
     */
    EncryptFilter create(String encryptConfig);

}
