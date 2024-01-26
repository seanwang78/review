package com.uaepay.gateway.cgs.domainservice.encrypt.impl;

import com.alibaba.fastjson.JSONObject;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptFilter;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 加密过滤组合
 * 
 * @author zc
 */
public class ComposedEncryptFilter implements EncryptFilter {

    public ComposedEncryptFilter(List<EncryptFilter> filters) {
        this.filters = filters;
    }

    List<EncryptFilter> filters;

    @Override
    public void filterReplace(JSONObject body, ContextParameter parameter) {
        if (body == null || body.isEmpty()) {
            return;
        }
        if (CollectionUtils.isEmpty(filters)) {
            return;
        }
        for (EncryptFilter filter : filters) {
            filter.filterReplace(body, parameter);
        }
    }
}
