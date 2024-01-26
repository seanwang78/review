package com.uaepay.pub.csc.domainservice.data.mongo;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.uaepay.pub.csc.domain.data.MongoDataSourceConfig;
import com.uaepay.pub.csc.domainservice.data.MongoTemplateFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 */
@Slf4j
@Service
public class MongoTemplateFactoryImpl implements MongoTemplateFactory {

    @Autowired
    private Environment environment;

    private final ConcurrentHashMap<String, MongoTemplate> dataSourceMap = new ConcurrentHashMap<>();

    @Override
    public MongoTemplate getOrCreate(MongoDataSourceConfig dataSourceConfig, String databaseName) {
        String key = dataSourceConfig.getCode() + "-" + databaseName;
        MongoTemplate result = dataSourceMap.get(key);
        if (result != null) {
            return result;
        }
        synchronized (dataSourceMap) {
            result = dataSourceMap.get(key);
            if (result != null) {
                return result;
            }
            result = build(dataSourceConfig, databaseName);
            dataSourceMap.put(key, result);
        }
        return result;
    }

    private MongoTemplate build(MongoDataSourceConfig dataSourceConfig, String databaseName) {
        log.info("创建mongoTemplate: {}, {}", dataSourceConfig.getCode(), databaseName);
        MongoClientFactory clientFactory = new MongoClientFactory(dataSourceConfig, environment, new ArrayList<>());
        MongoClient client = clientFactory.createMongoClient(null);
        MongoDatabaseFactory mongoDbFactory = new SimpleMongoClientDatabaseFactory(client, databaseName);
        return new MongoTemplate(mongoDbFactory, null);
    }

}
