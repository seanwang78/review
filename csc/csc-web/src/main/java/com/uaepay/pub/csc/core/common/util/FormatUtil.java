package com.uaepay.pub.csc.core.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.uaepay.pub.csc.domain.data.RowData;

/**
 * 格式化工具
 * 
 * @author zc
 */
public final class FormatUtil {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public static String datetime(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    public static String formatObject(Object object) {
        if(object==null){
            return "";
        }
        if(object instanceof BigDecimal){
            DecimalFormat decimalFormat=new DecimalFormat(",###");
            if(object.toString().contains(".")){
                return decimalFormat.format(new BigDecimal(object.toString().split("\\.")[0]))+"."+object.toString().split("\\.")[1];
            }
            else{
                return decimalFormat.format(new BigDecimal(object.toString()));
            }
        }
        else{
            return object.toString();
        }
    }

    public static String duration(Date begin, Date end) {
        if (begin == null || end == null) {
            return null;
        }
        Duration duration = Duration.ofMillis(end.getTime() - begin.getTime());
        return duration.toString();
    }

    public static String composeList(List<String> list, String brace) {
        List<String> values = new ArrayList<>();
        if (list != null) {
            for (String value : list) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(value)) {
                    values.add(value);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (values.size() != 0) {
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
        sb.append("]");
        return sb.toString();
    }

    public static String upper(String s) {
        return StringUtils.upperCase(s);
    }

    public static String rowDataList(List<RowData> rowDataList) {
        if (CollectionUtils.isEmpty(rowDataList)) {
            return null;
        }
        if (rowDataList.size() == 1) {
            return rowDataList.get(0) + "";
        }
        return StringUtils.join(rowDataList, "<br/>");
    }
}
