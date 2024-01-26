package com.uaepay.gateway.cgs.domainservice.encrypt;

import com.alibaba.fastjson.JSONObject;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;

/**
 * 将API配置的加密字段解密后使用UES加密
 * 
 * @author zc
 */
public interface EncryptFilter {

    /**
     * 将body中的加密信息按加密配置做替换
     *
     * @param body
     *            业务请求
     * @param parameter
     *            盐
     */
    void filterReplace(JSONObject body, ContextParameter parameter);

}
