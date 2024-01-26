package com.uaepay.pub.csc.domain.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.uaepay.pub.csc.domain.data.MongoDataSourceConfig;

import lombok.Data;

/**
 * @author zc
 */
@Data
@ConfigurationProperties(prefix = "datasource")
public class DataSourceProperties {

    /**
     * mysql配置列表
     */
    String[] mysql;

    String mongo = "mongo_reader";

    /**
     * mongo配置列表
     */
    List<MongoDataSourceConfig> mongoConfigs;

    /**
     * es 业务数据
     */
//    String esBusiness = "es_business_reader";

}
