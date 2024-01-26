package com.uaepay.gateway.cgs.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * 工具类 .
 * <p>
 *
 * @author yusai
 * @date 2019-12-28 10:32.
 */
@Slf4j
public class CommonUtils {

    /**
     * @Title: getIpAddress
     * @Description: 获取ip地址
     * @param request
     * @return: String
     */
    public static   String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // x-forwarded-for 在多层代理情况下会有多个IP，格式：IP0,IP1,IP2; 第一个为客户端真实ip，其余为代理服务器IP
        if(StringUtils.isNotBlank(ip)) {
            ip = ip.split(",")[0];
        }
        return ip;
    }


    public static String WARN_LOG_FORMAT =  "especially_log_prefix:%s";

}
