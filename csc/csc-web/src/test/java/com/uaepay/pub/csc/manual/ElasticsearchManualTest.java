package com.uaepay.pub.csc.manual;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.LoggingDeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientProperties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.util.JsonUtil;
import com.uaepay.pub.csc.core.common.util.FormatUtil;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.test.base.ManualTestBase;
import com.uaepay.pub.csc.test.builder.TestDataBuilder;
import com.uaepay.pub.csc.test.dal.es.PropertiesCredentialsProvider;
import com.uaepay.pub.csc.test.domain.TestData;

@Disabled
public class ElasticsearchManualTest extends ManualTestBase {

    @Autowired
    DataSourceConfigFactory dataSourceConfigFactory;

    RestHighLevelClient client;

    @BeforeAll
    public void init() {
        DataSourceConfig dataSourceConfig = dataSourceConfigFactory.getOrCreate("es_business_reader");
        if (dataSourceConfig instanceof EsDataSourceConfig) {
            client = ((EsDataSourceConfig)dataSourceConfig).getRestHighLevelClient();
        }
    }

    @Test
    public void testIndex() throws IOException {
        TestData data = TestDataBuilder.newSrcData("data_set_1", "a", "S", "1.23").build();
        IndexRequest request =
            new IndexRequest("i_dev_csc_test_data").id("1").source(JsonUtil.toJsonString(data), XContentType.JSON);
        System.out.println("request: " + request);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("response: " + response);
    }

    @Test
    public void testIndex2() throws IOException {

        TestData data = TestDataBuilder.newMonitorData("hunan", "cs", "csx", "data_set_1", "a", "S", "1.23").build();
        IndexRequest request =
            new IndexRequest("i_dev_csc_test_data").id("2").source(JsonUtil.toJsonString(data), XContentType.JSON);
        System.out.println("request: " + request);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("response: " + response);
    }

    /**
     * DeleteResponse[index=i_dev_csc_test_data,type=_doc,id=a,version=2,result=deleted,shards=ShardInfo{total=2,
     * successful=2, failures=[]}]
     * DeleteResponse[index=i_dev_csc_test_data,type=_doc,id=a,version=1,result=not_found,shards=ShardInfo{total=2,
     * successful=2, failures=[]}]
     */
    @Test
    public void testDeleteById() throws IOException {
        DeleteRequest request = new DeleteRequest("i_dev_csc_test_data", "a");
        System.out.println("request: " + request);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println("response: " + response);
    }

    /**
     * BulkByScrollResponse[took=11ms,timed_out=false,sliceId=null,updated=0,created=0,deleted=2,batches=1,versionConflicts=0,noops=0,retries=0,throttledUntil=0s,bulk_failures=[],search_failures=[]]
     * BulkByScrollResponse[took=81ms,timed_out=false,sliceId=null,updated=0,created=0,deleted=0,batches=0,versionConflicts=0,noops=0,retries=0,throttledUntil=0s,bulk_failures=[],search_failures=[]]
     */
    @Test
    public void testDeleteByCondition() throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest("i_dev_csc_test_data");
        request.setQuery(QueryBuilders.matchQuery("dataSet", "data_set_1"));
        System.out.println("request: " + request);
        BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
        System.out.println("response: " + response);
    }

    @Test
    public void testWrapperQuery() throws IOException {
        SearchRequest request = new SearchRequest("i_dev_csc_test_data");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.wrapperQuery("{\n" + "    \"bool\": {\n" + "      \"filter\": [\n"
            + "        {\"match\": {\"dataSet\": \"TestDataMockerTest\"}},\n"
            + "        {\"match\": {\"dataType\": \"S\"}},\n"
            + "        {\"range\": {\"updateTime\": {\"gte\": \"2020-12-01T00:00:00.000Z\", \"lt\": \"2020-12-01T04:00:00.000Z\"}}}\n"
            + "      ]\n" + "    }\n" + "  }"));
        sourceBuilder.fetchSource(new String[] {"orderNo", "status", "amount", "updateTime"}, null);
        sourceBuilder.sort("orderNo.keyword", SortOrder.ASC).from(0).size(2);
        request.source(sourceBuilder);
        System.out.println("request: " + request);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println("response: " + response);
    }

    @Test
    public void testJsonQuery() throws IOException {
        SearchRequest request = new SearchRequest("i_dev_csc_test_data");
        String jsonQuery = "{\n" + "  \"query\": {\n" + "    \"bool\": {\n" + "      \"filter\": [\n"
            + "        {\"match\": {\"dataSet\": \"TestDataMockerTest\"}},\n"
            + "        {\"match\": {\"dataType\": \"S\"}},\n"
            + "        {\"range\": {\"updateTime\": {\"gte\": \"2020-12-01T00:00:00.000Z\", \"lt\": \"2020-12-01T04:00:00.000Z\"}}}\n"
            + "      ]\n" + "    }\n" + "  },\n"
            + "  \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"],\n"
            + "  \"sort\": {\"orderNo.keyword\": \"asc\"},\n" + "  \"from\": 0,\n" + "  \"size\": 4\n" + "}";
        JSONObject jsonObject = JSON.parseObject(jsonQuery);
        JSONObject queryObject = (JSONObject)jsonObject.remove("query");
        System.out.println("queryObject: " + queryObject);
        System.out.println("jsonObject: " + jsonObject);
        XContentParser jsonParser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
            LoggingDeprecationHandler.INSTANCE, jsonObject.toJSONString());
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.fromXContent(jsonParser);
        sourceBuilder.query(QueryBuilders.wrapperQuery(queryObject.toJSONString()));
        request.source(sourceBuilder);
        System.out.println("request: " + request);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        print(response);
    }

    @Test
    public void testAggregations() throws IOException {
        SearchRequest request = new SearchRequest("i_dev_csc_test_data");
        String jsonQuery = "{\n" + "  \"query\": {\n" + "    \"bool\": {\n" + "      \"filter\": [\n"
            + "        {\"match\": {\"dataSet\": \"TestDataMockerTest\"}},\n"
            + "        {\"match\": {\"dataType\": \"S\"}},\n"
            + "        {\"range\": {\"updateTime\": {\"gte\": \"2020-12-01T00:00:00.000Z\", \"lt\": \"2020-12-01T04:00:00.000Z\"}}}\n"
            + "      ]\n" + "    }\n" + "  },\n"
            + "  \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"],\n"
            + "  \"sort\": {\"orderNo.keyword\": \"asc\"},\n" + "  \"from\": 0,\n" + "  \"size\": 4\n" + "}";
        JSONObject jsonObject = JSON.parseObject(jsonQuery);
        JSONObject queryObject = (JSONObject)jsonObject.remove("query");
        System.out.println("queryObject: " + queryObject);
        System.out.println("jsonObject: " + jsonObject);
        XContentParser jsonParser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
            LoggingDeprecationHandler.INSTANCE, jsonObject.toJSONString());
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.fromXContent(jsonParser);
        sourceBuilder.query(QueryBuilders.wrapperQuery(queryObject.toJSONString()));

        AggregationBuilder term = AggregationBuilders.terms("test").field("dataType.keyword");
        sourceBuilder.aggregation(term);

        System.out.println(sourceBuilder.toString());
        request.source(sourceBuilder);
        System.out.println("request: " + request);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        print(response);

    }

    private void print(SearchResponse response) {
        System.out.println("status: " + response.status());
        SearchHits hitList = response.getHits();
        SearchHit[] hits = hitList.getHits();
        for (SearchHit hit : hits) {
            System.out.println("id: " + hit.getId());
            System.out.println("getSourceAsMap: " + hit.getSourceAsMap());
            System.out.println("getSourceAsString: " + hit.getSourceAsString());
            Iterator<Map.Entry<String, Object>> iterator = hit.getSourceAsMap().entrySet().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                Object value = entry.getValue();
                System.out.printf("field %d: %s = %s, type: %s\n", i++, entry.getKey(), value, value.getClass());
                if (value instanceof Double) {
                    System.out.println("double to bigdecimal: " + BigDecimal.valueOf((double)value));
                }
            }
        }
        System.out.println("response: " + response);

        ColumnData columnData = ColumnData.fromEs(hits, "orderno");
        System.out.println("columnData: " + columnData);

        List<RowData> rowData = RowDataConverter.fromEs(hits, columnData);
        System.out.println("rowData: " + FormatUtil.rowDataList(rowData));
    }

    private RestHighLevelClient createClient() {
        ElasticsearchRestClientProperties properties = new ElasticsearchRestClientProperties();
        properties.setUris(
            Arrays.asList("esdata1.test2pay.com:9200", "esdata2.test2pay.com:9200", "esdata3.test2pay.com:9200"));
        properties.setUsername("devuser");
        properties.setPassword("devuser");

        HttpHost[] hosts = properties.getUris().stream().map(HttpHost::create).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(hosts);
        builder.setHttpClientConfigCallback((httpClientBuilder) -> {
            httpClientBuilder.setDefaultCredentialsProvider(new PropertiesCredentialsProvider(properties));
            return httpClientBuilder;
        });
        return new RestHighLevelClient(builder);
    }

}
