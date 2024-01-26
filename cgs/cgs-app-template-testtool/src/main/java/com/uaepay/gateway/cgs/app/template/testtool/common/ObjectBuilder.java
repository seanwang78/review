package com.uaepay.gateway.cgs.app.template.testtool.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author zc
 */
public class ObjectBuilder {

    JSONObject result = new JSONObject();

    public ObjectBuilder param(String key, Object value) {
        if (value instanceof ObjectBuilder) {
            result.put(key, ((ObjectBuilder)value).build());
        } else {
            result.put(key, value);
        }
        return this;
    }

    public ObjectBuilder paramDate(String key, Date date) {
        result.put(key, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(date));
        return this;
    }

    /**
     * 设置时间参数
     * 
     * @param key
     *            参数名称
     * @param date
     *            日期
     * @param timeZoneId
     *            时区ID，ex. +04:00,-07:00
     */
    public ObjectBuilder paramDate(String key, Date date, String timeZoneId) {
        result.put(key,
            FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ", TimeZone.getTimeZone(ZoneId.of(timeZoneId))).format(date));
        return this;
    }

    public ObjectBuilder paramDateNow(String key) {
        return paramDate(key, new Date());
    }

    public JSONObject build() {
        return result;
    }

}
