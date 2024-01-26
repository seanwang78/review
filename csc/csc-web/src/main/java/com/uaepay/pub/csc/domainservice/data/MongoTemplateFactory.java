package com.uaepay.pub.csc.domainservice.data;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.uaepay.pub.csc.domain.data.MongoDataSourceConfig;

/**
 * @author zc
 */
public interface MongoTemplateFactory {

    /**
     * 获取MongoTemplate
     *
     * @param dataSourceConfig
     *            数据源配置
     * @param databaseName
     *            数据库名称
     * @return mongoTemplate
     * @throws Exception
     *             数据源异常时抛出
     */
    MongoTemplate getOrCreate(MongoDataSourceConfig dataSourceConfig, String databaseName);

}
