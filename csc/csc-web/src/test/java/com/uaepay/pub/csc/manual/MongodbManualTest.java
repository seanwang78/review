package com.uaepay.pub.csc.manual;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import com.mongodb.client.MongoClient;
import com.uaepay.pub.csc.core.common.util.FormatUtil;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.data.RowDataConverter;
import com.uaepay.pub.csc.test.base.ManualTestBase;

@Disabled
public class MongodbManualTest extends ManualTestBase {

    @Autowired
    MongoClient mongoClient;

    @Autowired
    MongoConverter mongoConverter;

    @Test
    public void testFind() {
        List<Document> batch = find("{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: 'csc_unit_test'}"
            + ", {updateTime: {$gte: ISODate('2020-01-01T00:00:00Z'), $lt: ISODate('2020-01-01T10:00:00Z')}}"
            + "]}, projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1}, sort: {updateTime: 1}}");
        ColumnData columnData = ColumnData.fromMongo(batch, "orderno");
        System.out.println(columnData);

        List<RowData> rowData = RowDataConverter.fromMongo(batch, columnData);
        System.out.println(FormatUtil.rowDataList(rowData));
    }

    // @Test
    // public void testFind() {
    // List<Document> batch = find("{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: 'csc_unit_test'}]}"
    // + ", projection: {_id: 0, orderNo: 1, status: 1, amount: 1}}");
    // ColumnData columnData = ColumnData.fromMongo(batch, "orderno");
    // System.out.println(columnData);
    // List<RowData> rowData = RowDataConverter.fromMongo(batch, columnData);
    // System.out.println(FormatUtil.rowDataList(rowData));
    // }

    private List<Document> find(String jsonCommand) {
        // 构建查询指令
        Document document = Document.parse(jsonCommand);
        String databaseName = (String)document.remove("db");
        document.append("batchSize", 10);
        System.out.println("command document: " + document);

        // 执行查询
        MongoTemplate mongoTemplate = createTemplate(databaseName);
        Document result = mongoTemplate.executeCommand(document);

        // 解析结果
        Document cursor = result.get("cursor", Document.class);
        List<Document> batch = cursor.getList("firstBatch", Document.class);
        System.out.println("batch: " + batch);
        return batch;
    }

    // private void apiFind(String databaseName) {
    // MongoTemplate mongoTemplate = createTemplate(databaseName);
    // mongoTemplate.find()
    // }

    private MongoTemplate createTemplate(String databaseName) {
        MongoDatabaseFactory mongoDbFactory = new SimpleMongoClientDatabaseFactory(mongoClient, databaseName);
        return new MongoTemplate(mongoDbFactory, mongoConverter);
    }

}
