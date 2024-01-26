package com.payby.gateway.openapi;

import com.alibaba.fastjson.TypeReference;
import com.payby.gateway.openapi.constant.Method;

public interface Api<R> {

    String getApi();

    Method getMethod();

    String getVersion();

    TypeReference<R> getResponseType();

}
