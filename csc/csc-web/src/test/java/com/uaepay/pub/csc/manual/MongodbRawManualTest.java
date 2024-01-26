package com.uaepay.pub.csc.manual;

import java.util.Iterator;
import java.util.List;

import com.uaepay.pub.csc.domainservice.data.mongo.JsonAggregationOperation;
import org.bson.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Disabled
public class MongodbRawManualTest {

    public static final String FIND_EXPRESSION = "{find:'csc_test_data', filter: {$and: [{dataSet:'csc_unit_test'}]}"
        + ", projection:{_id:0,orderNo:1,status:1,amount:1}, batchSize:500}";
    public static final String AGGREGATE_EXPRESSION =
        "{aggregate: 'csc_test_data', pipeline: [" + "{$match: {$and: [{dataType: 'M'}]}}"
            + ", {$group: {_id: {dataSet: '$dataSet'}, count: {$sum: 1}, sum: {$sum: '$amount'}}}"
            + ", {$project: {_id: 0, dataSet: '$_id.dataSet', count: 1, sum: 1}}" + "]}";

    @Test
    public void testRaw() {
        Document result = find();
        System.out.println(result);
        Document cursor = result.get("cursor", Document.class);
        System.out.printf("cursor: %s\n", cursor);
        List<Document> firstBatch = cursor.getList("firstBatch", Document.class);
        System.out.printf("firstBatch size: %d\n", firstBatch.size());
        for (Document doc : firstBatch) {
            System.out.println(doc);
        }
    }

    @Test
    public void testAggregate() {
        Document result = aggregate();
        System.out.println(result);
        List<Document> firstBatch = result.getList("result", Document.class);
        System.out.printf("firstBatch size: %d\n", firstBatch.size());
        for (Document doc : firstBatch) {
            System.out.println(doc);
        }
    }

    @Test
    public void testAggregate_useAggregateApi() {
        MongoTemplate template = createTemplate("testdb");
        MatchOperation match = Aggregation.match(new Criteria("dataType").is("M"));
        GroupOperation group = Aggregation.group("dataType", "dataSet").count().as("count").sum("$amount").as("sum");
        ProjectionOperation project = Aggregation.project().andExpression("_id.dataType").as("dataType")
            .andExpression("_id.dataSet").as("dataSet").andInclude("count", "sum").andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(match, group, project);
        AggregationResults<Object> result = template.aggregate(aggregation, "csc_test_data", Object.class);

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            System.out.println(object.getClass() + " - " + object);
        }
    }

    @Test
    public void testAggregate_useAggregateApi_simple() {
        MongoTemplate template = createTemplate("testdb");
        MatchOperation match = Aggregation.match(new Criteria("dataType").is("M"));
        GroupOperation group = Aggregation.group().count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(match, group);
        AggregationResults<Object> result = template.aggregate(aggregation, "csc_test_data", Object.class);

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            System.out.println(object.getClass() + " - " + object);
        }
    }

    /**
     * <pre>
     * Executing aggregation: [{ "$match" : { "dataType" : "M"}}, { "$group" : { "_id" : null, "count" : { "$sum" : 1}}}] in collection csc_test_data
     * </pre>
     */
    @Test
    public void testAggregate_customOperation() {
        MongoTemplate template = createTemplate("testdb");

        String pipeline = "{pipeline: [" + "{$match: {$and: [{dataType: 'M'}]}}"
            + ", {$group: {_id: {dataSet: '$dataSet'}, count: {$sum: 1}, sum: {$sum: '$amount'}}}"
            + ", {$project: {_id: 0, dataSet: '$_id.dataSet', count: 1, sum: 1}}" + "]}";

        Aggregation aggregation = Aggregation.newAggregation(new JsonAggregationOperation(pipeline));
        AggregationResults<Object> result = template.aggregate(aggregation, "csc_test_data", Object.class);

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            System.out.println(object.getClass() + " - " + object);
        }
    }

    private Document find() {
        MongoTemplate template = createTemplate("testdb");
        return template.executeCommand(FIND_EXPRESSION);
    }

    private Document aggregate() {
        MongoTemplate template = createTemplate("testdb");
        return template.executeCommand(AGGREGATE_EXPRESSION);
    }

    /**
     * '$and': [{'dataSet':'csc_unit_test'}]
     */
    private Document queryBatchSize() {
        MongoTemplate template = createTemplate("billing");
        return template.executeCommand("{'find':'t_day_balance', 'filter': {}, 'batchSize':500, 'limit': 500"
            + ", 'projection':{'accountNo':1, 'openingBalance':1, '_id':0}}");
    }

    private MongoTemplate createTemplate(String databaseName) {
        MongoClient client = MongoClients.create(
            "mongodb://reader:reader_IkS4kFeZ0BgcAB@mongofunc1.test2pay.com:8635,mongofunc2.test2pay.com:8635/admin?authMode=scram-sha1&replica-set=replica&authSource=admin&readPreference=secondaryPreferred");

        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(client, databaseName), null);
    }

}
