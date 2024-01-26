package com.uaepay.pub.csc.domainservice.data;

import java.util.Date;
import java.util.List;

import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;

/**
 * SQL模版替换器
 * 
 * @author zc
 */
public interface SqlTemplateReplacer {

    /**
     * 替换字符串
     */
    SqlTemplateReplacer replaceString(SqlTemplateParamEnum sqlTemplateParamEnum, String str);

    /**
     * 替换日期
     */
    SqlTemplateReplacer replaceDate(SqlTemplateParamEnum sqlTemplateParamEnum, Date date);

    /**
     * 替换数字
     */
    SqlTemplateReplacer replaceNumber(SqlTemplateParamEnum sqlTemplateParamEnum, int number);

    /**
     * 替换字符串列表
     */
    SqlTemplateReplacer replaceStringList(SqlTemplateParamEnum sqlTemplateParamEnum, List<String> list);

    /**
     * 替换数字列表
     */
    SqlTemplateReplacer replaceNumberList(SqlTemplateParamEnum sqlTemplateParamEnum, List<String> list);

    /**
     * 获取替换后的sql
     */
    String getSql();

}
