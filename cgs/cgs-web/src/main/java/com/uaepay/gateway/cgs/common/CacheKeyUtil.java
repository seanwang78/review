package com.uaepay.gateway.cgs.common;

import org.apache.commons.lang.StringUtils;

/**
 * <p>Cache键值生成工具</p>
 */
public class CacheKeyUtil {
    /**
     * 缓存KEY分隔符
     */
    public final static String separator = "_";

    /**
     * 获取缓存键值
     * @param param
     * @return
     */
    public static String getKey(String... param) {

        if ((param == null) || (param.length == 0)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if(param != null && param.length >0){
            for (String p : param) {
                if(!StringUtils.isEmpty(p)){
                    sb.append(p);
                }else{
                    sb.append("");
                }
                sb.append(separator);
            }
        }

        if (sb.length() == 0) {
            return null;
        }

        return sb.toString().toLowerCase();
    }

}
