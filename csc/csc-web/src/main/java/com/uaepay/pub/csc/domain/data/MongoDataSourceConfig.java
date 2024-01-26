package com.uaepay.pub.csc.domain.data;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;

/**
 * mongo数据源配置
 * 
 * @author zc
 */
public class MongoDataSourceConfig extends MongoProperties implements DataSourceConfig {

    String code;

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
