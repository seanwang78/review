package com.uaepay.pub.csc.core.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.data.RowDataConverter;
import com.uaepay.pub.csc.domainservice.data.mongo.JsonAggregationOperation;

/**
 * @author zc
 */
public class MongoUtil {

    public static final String DB = "db";
    public static final String SKIP = "skip";
    public static final String BATCH_SIZE = "batchSize";
    public static final String CURSOR = "cursor";
    public static final String FIRST_BATCH = "firstBatch";
    public static final String RESULT = "result";
    public static final String FIND = "find";
    public static final String AGGREGATE = "aggregate";
    public static final String PIPELINE = "pipeline";
    public static final String DOLLAR_SKIP = "$skip";
    public static final String DOLLAR_LIMIT = "$limit";

    public static String extractDatabaseName(String sqlTemplate, String templateName) {
        String databaseName;
        try {
            Document command = Document.parse(sqlTemplate);
            databaseName = (String)command.get(DB);
        } catch (Throwable e) {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, templateName + " error");
        }
        if (StringUtils.isBlank(databaseName)) {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, templateName + " 'db' not exist");
        }
        return databaseName;
    }

    public static void appendSkipLimit(Document query, int skip, int limit) {
        if (query.containsKey(FIND)) {
            query.append(SKIP, skip).append(BATCH_SIZE, limit);
        } else if (query.containsKey(AGGREGATE)) {
            List<Document> pipeline = (List<Document>)query.get(PIPELINE);
            if (pipeline == null) {
                throw new FailException(CommonReturnCode.CONFIG_ERROR, "'pipeline' missing");
            }
            pipeline.add(new Document(DOLLAR_SKIP, skip));
            pipeline.add(new Document(DOLLAR_LIMIT, limit));
        } else {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "query not supported");
        }
    }

    public static void appendLimit(Document query, int limit) {
        if (query.containsKey(FIND)) {
            query.append(BATCH_SIZE, limit);
        } else if (query.containsKey(AGGREGATE)) {
            List<Document> pipeline = (List<Document>)query.get(PIPELINE);
            if (pipeline == null) {
                throw new FailException(CommonReturnCode.CONFIG_ERROR, "'pipeline' missing");
            }
            pipeline.add(new Document(DOLLAR_LIMIT, limit));
        } else {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "query not supported");
        }
    }

    public static List<RowData> execute(MongoTemplate mongoTemplate, Document query, String relateField) {
        List<RowData> rows;
        if (query.containsKey(MongoUtil.AGGREGATE)) {
            List<Map<String, Object>> batch = executeAggregate(mongoTemplate, query);
            ColumnData columnData = ColumnData.fromMongoAgg(batch, relateField);
            rows = RowDataConverter.fromMongoAgg(batch, columnData);
        } else {
            List<Document> batch = executeFind(mongoTemplate, query);
            ColumnData columnData = ColumnData.fromMongo(batch, relateField);
            rows = RowDataConverter.fromMongo(batch, columnData);
        }
        return rows;
    }

    private static List<Map<String, Object>> executeAggregate(MongoTemplate mongoTemplate, Document query) {
        String collectionName = query.getString(MongoUtil.AGGREGATE);
        String json = query.toJson();
        Aggregation aggregation = Aggregation.newAggregation(new JsonAggregationOperation(json));
        AggregationResults<Object> aggResults = mongoTemplate.aggregate(aggregation, collectionName, Object.class);

        List<Map<String, Object>> results = new ArrayList<>();
        Iterator<Object> iterator = aggResults.iterator();
        while (iterator.hasNext()) {
            results.add((Map)iterator.next());
        }
        return results;
    }

    private static List<Document> executeFind(MongoTemplate mongoTemplate, Document query) {
        Document result = mongoTemplate.executeCommand(query);
        if (result.containsKey(CURSOR)) {
            Document cursor = result.get(CURSOR, Document.class);
            return (List<Document>)cursor.get(FIRST_BATCH);
        } else if (result.containsKey(RESULT)) {
            return (List<Document>)result.get(RESULT);
        } else {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "result error: " + result);
        }
    }

}
