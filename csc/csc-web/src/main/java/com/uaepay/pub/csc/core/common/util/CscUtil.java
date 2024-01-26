package com.uaepay.pub.csc.core.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * 运营对账工具类
 * @author zc
 */
public class CscUtil {

    private static final String SEPARATOR = "[,，|]";

    /**
     * 分隔字符串，支持英文、中文逗号和竖线
     * @param values
     * @return
     */
    public static List<String> splitString(String values) {
        if (StringUtils.isBlank(values)) {
            return new ArrayList<>();
        }
        String[] splits = values.split(SEPARATOR);
        return Arrays.stream(splits).filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toList());
    }

}
