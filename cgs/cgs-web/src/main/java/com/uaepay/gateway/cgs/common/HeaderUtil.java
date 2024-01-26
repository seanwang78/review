package com.uaepay.gateway.cgs.common;

import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.constants.HttpHeaderKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/9/7
 * @since 0.1
 */
@Slf4j
public class HeaderUtil {


    public static List<String> HTTP_HEADER_KEYS = new ArrayList<>();
    static {

        List<String> httpHeaderKeys = new ArrayList<>();
        List<Class<?>> classes = Arrays.asList(HttpHeaderKey.class, GatewayConstants.class);
        for (Class<?> aClass : classes) {
            Field[] fields = aClass.getFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object o = field.get(null);
                    if (o != null) {
                        httpHeaderKeys.add(o.toString().toLowerCase());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        HTTP_HEADER_KEYS.addAll(httpHeaderKeys);
    }

    public static Map<String, String> getHeadersInfo(HttpServletRequest request, boolean enableFilter) {
        Map<String, String> map = new HashMap<>();
        try {
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = (String) headerNames.nextElement();
                key = StringUtils.trim(key.toLowerCase());
                if (enableFilter && !HTTP_HEADER_KEYS.contains(key)) {
                    continue;
                }
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }catch (Throwable e) {
            log.warn("Convert Http Header exception ",e);
        }
        return map;
    }
}
