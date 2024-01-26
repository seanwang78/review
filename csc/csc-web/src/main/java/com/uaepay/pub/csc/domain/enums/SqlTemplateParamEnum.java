package com.uaepay.pub.csc.domain.enums;

import java.util.regex.Pattern;

/**
 * sql模版参数枚举
 * 
 * @author zc
 */
public enum SqlTemplateParamEnum {

    /** 开始时间 */
    BEGIN_TIME("\\{begin\\}"),

    /** 结束时间 */
    END_TIME("\\{end\\}"),

    /** MySQL偏移，从0开始 */
    OFFSET("\\{offset\\}"),

    /** 分页大小 */
    COUNT("\\{count\\}"),

    /** 字符串订单号 */
    ID_STRING("\\{id_s\\}"),

    /** 数字订单号 */
    ID_NUMBER("\\{id_n\\}"),

    ;

    private SqlTemplateParamEnum(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    public boolean find(String sqlTemplate) {
        return this.pattern.matcher(sqlTemplate).find();
    }

    private final String regex;

    private final Pattern pattern;

    public String getRegex() {
        return regex;
    }

    public Pattern getPattern() {
        return pattern;
    }

}
