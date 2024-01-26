package com.uaepay.gateway.cgs.common;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/10/20
 * @since 0.1
 */
public class RequestUtil {


    public static String getHeader(HttpServletRequest request, String firstKey, String secondKey) {
        if(StringUtils.isBlank(request.getHeader(firstKey))) {
            return request.getHeader(secondKey);
        }
        return request.getHeader(firstKey);
    }
}
