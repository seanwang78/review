package com.uaepay.pub.csc.domainservice.data.es;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;

import com.uaepay.pub.csc.core.common.util.FormatUtil;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domainservice.data.SqlTemplateReplacer;

/**
 * @author zc
 */
public class EsSqlTemplateReplacer implements SqlTemplateReplacer {

    private static final FastDateFormat DATE_FORMAT =
        FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssX", TimeZone.getTimeZone("UTC"));

    public EsSqlTemplateReplacer(String sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    private String sqlTemplate;

    @Override
    public SqlTemplateReplacer replaceString(SqlTemplateParamEnum sqlTemplateParamEnum, String str) {
        String replacement = "\"" + str + "\"";
        sqlTemplate = sqlTemplateParamEnum.getPattern().matcher(sqlTemplate).replaceAll(replacement);
        return this;
    }

    @Override
    public SqlTemplateReplacer replaceDate(SqlTemplateParamEnum sqlTemplateParamEnum, Date date) {
        String replacement = "\"" + DATE_FORMAT.format(date) + "\"";
        sqlTemplate = sqlTemplateParamEnum.getPattern().matcher(sqlTemplate).replaceAll(replacement);
        return this;
    }

    @Override
    public SqlTemplateReplacer replaceNumber(SqlTemplateParamEnum sqlTemplateParamEnum, int number) {
        sqlTemplate = sqlTemplateParamEnum.getPattern().matcher(sqlTemplate).replaceAll(Integer.toString(number));
        return this;
    }

    @Override
    public SqlTemplateReplacer replaceStringList(SqlTemplateParamEnum sqlTemplateParamEnum, List<String> list) {
        sqlTemplate =
            sqlTemplateParamEnum.getPattern().matcher(sqlTemplate).replaceAll(FormatUtil.composeList(list, "\""));
        return this;
    }

    @Override
    public SqlTemplateReplacer replaceNumberList(SqlTemplateParamEnum sqlTemplateParamEnum, List<String> list) {
        sqlTemplate =
            sqlTemplateParamEnum.getPattern().matcher(sqlTemplate).replaceAll(FormatUtil.composeList(list, null));
        return this;
    }

    @Override
    public String getSql() {
        return sqlTemplate;
    }

}
