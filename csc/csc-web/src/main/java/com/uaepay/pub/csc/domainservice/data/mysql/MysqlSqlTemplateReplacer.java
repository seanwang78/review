package com.uaepay.pub.csc.domainservice.data.mysql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domainservice.data.SqlTemplateReplacer;

/**
 * @author zc
 */
public class MysqlSqlTemplateReplacer implements SqlTemplateReplacer {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public MysqlSqlTemplateReplacer(String sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    private String sqlTemplate;

    @Override
    public SqlTemplateReplacer replaceString(SqlTemplateParamEnum sqlTemplateParamEnum, String str) {
        String replacement = "'" + str + "'";
        sqlTemplate = sqlTemplateParamEnum.getPattern().matcher(sqlTemplate).replaceAll(replacement);
        return this;
    }

    @Override
    public SqlTemplateReplacer replaceDate(SqlTemplateParamEnum sqlTemplateParamEnum, Date date) {
        String replacement = "str_to_date('" + DATE_FORMAT.format(date) + "', '%Y-%m-%d %H:%i:%s')";
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
        sqlTemplate = sqlTemplateParamEnum.getPattern().matcher(sqlTemplate).replaceAll(composeList(list, "'"));
        return this;
    }

    @Override
    public SqlTemplateReplacer replaceNumberList(SqlTemplateParamEnum sqlTemplateParamEnum, List<String> list) {
        sqlTemplate = sqlTemplateParamEnum.getPattern().matcher(sqlTemplate).replaceAll(composeList(list, null));
        return this;
    }

    private String composeList(List<String> list, String brace) {
        List<String> values = new ArrayList<>();
        if (list != null) {
            for (String value : list) {
                if (StringUtils.isNotBlank(value)) {
                    values.add(value);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (values.size() == 0) {
            sb.append("null");
        } else {
            for (String value : values) {
                if (brace == null) {
                    sb.append(value);
                } else {
                    sb.append(brace).append(value).append(brace);
                }
                sb.append(",");
            }
            sb.setLength(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String getSql() {
        return sqlTemplate;
    }

}
