package com.uaepay.pub.csc.test.dal.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.result.DeleteResult;
import com.uaepay.pub.csc.test.configuration.TestProperties;
import com.uaepay.pub.csc.test.domain.TestData;

@Service
public class TestDataMongoMapper {

    public static final String DATABASE_NAME = "testdb";
    public static final String COLLECTION_NAME = "csc_test_data";

    public TestDataMongoMapper(TestProperties testProperties, Environment environment) {
        MongoProperties properties = new MongoProperties();
        properties.setUri(testProperties.getMongoTestDb());

        MongoClientFactory clientFactory = new MongoClientFactory(properties, environment, null);
        MongoClient client = clientFactory.createMongoClient(null);
        MongoDatabaseFactory dbFactory = new SimpleMongoClientDatabaseFactory(client, DATABASE_NAME);
        mongoTemplate = new MongoTemplate(dbFactory, null);
    }

    MongoTemplate mongoTemplate;

    public long deleteByDataSet(String dataSet) {
        DeleteResult result = mongoTemplate.remove(query(where("dataSet").is(dataSet)), COLLECTION_NAME);
        return result.getDeletedCount();
    }

    public void insertTestData(TestData testData) {
        mongoTemplate.insert(testData, COLLECTION_NAME);
    }

}
