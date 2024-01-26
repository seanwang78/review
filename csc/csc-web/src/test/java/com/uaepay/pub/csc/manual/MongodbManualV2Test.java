package com.uaepay.pub.csc.manual;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.uaepay.pub.csc.core.common.util.FormatUtil;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.MongoDataSourceConfig;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.data.RowDataConverter;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.domainservice.data.MongoTemplateFactory;
import com.uaepay.pub.csc.test.base.ManualTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

@Disabled
public class MongodbManualV2Test extends ManualTestBase {

    @Autowired
    DataSourceConfigFactory dataSourceConfigFactory;

    @Autowired
    MongoTemplateFactory mongoTemplateFactory;

    @Autowired
    TestDataMocker testDataMocker;

    @Test
    public void testFind() {
        MongoTemplate mongoTemplate = createMongoTemplate(TestConstants.DATASOURCE_MONGO_2, "testdb");

        testDataMocker.reset("csc_unit_test");
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2022-03-01T00:30:00Z").currency("AED");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2022-03-01T01:30:00Z").group1("G1");
        testDataMocker.mockMongo();

        List<Document> batch = find(mongoTemplate,
            "{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: 'csc_unit_test'}"
                + ", {updateTime: {$gte: ISODate('2022-03-01T00:00:00Z'), $lt: ISODate('2022-03-02T00:00:00Z')}}"
                + "]}, projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1, currency: 1, group1: 1}, sort: {updateTime: 1}}");
        ColumnData columnData = ColumnData.fromMongo(batch, null);
        System.out.printf("Column Data: %s\n", columnData);

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

    private MongoTemplate createMongoTemplate(String dataSourceCode, String db) {
        MongoDataSourceConfig config = (MongoDataSourceConfig)dataSourceConfigFactory.getOrCreate(dataSourceCode);
        return mongoTemplateFactory.getOrCreate(config, db);
    }

    private List<Document> find(MongoTemplate mongoTemplate, String jsonCommand) {
        // 构建查询指令
        Document document = Document.parse(jsonCommand);
        String databaseName = (String)document.remove("db");
        document.append("batchSize", 10);
        System.out.println("command document: " + document);

        // 执行查询
        Document result = mongoTemplate.executeCommand(document);

        // 解析结果
        Document cursor = result.get("cursor", Document.class);
        List<Document> batch = cursor.getList("firstBatch", Document.class);
        System.out.println("batch: " + batch);
        return batch;
    }

}
