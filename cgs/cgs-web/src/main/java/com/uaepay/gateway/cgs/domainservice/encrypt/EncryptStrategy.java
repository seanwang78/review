package com.uaepay.gateway.cgs.domainservice.encrypt;

import com.uaepay.basis.beacon.common.template.CodeService;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;

/**
 * 加密策略
 */
public interface EncryptStrategy extends CodeService {

    /**
     * 转化
     * 
     * @param fieldName
     *            字段名称
     * @param original
     *            原始值
     * @param parameter
     *            盐
     * @return 修改后的值
     */
    String convert(String fieldName, String original, ContextParameter parameter);

}
