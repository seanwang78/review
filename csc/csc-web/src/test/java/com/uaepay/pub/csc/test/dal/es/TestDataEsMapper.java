package com.uaepay.pub.csc.test.dal.es;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.cbor.CborXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientProperties;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.test.configuration.TestProperties;
import com.uaepay.pub.csc.test.domain.TestData;

@Repository
public class TestDataEsMapper {

    public static final String INDEX_NAME = "i_dev_csc_test_data";

    public TestDataEsMapper(TestProperties testProperties) {
        ElasticsearchRestClientProperties devProperties = new ElasticsearchRestClientProperties();
        devProperties.setUris(testProperties.getEsUris());
        devProperties.setUsername(testProperties.getEsUsername());
        devProperties.setPassword(testProperties.getEsPassword());

        HttpHost[] hosts = devProperties.getUris().stream().map(HttpHost::create).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(hosts);

        if (StringUtils.isNotBlank(testProperties.getEsUsername())
            && StringUtils.isNotBlank(testProperties.getEsPassword())) {
            builder.setHttpClientConfigCallback((httpClientBuilder) -> {
                httpClientBuilder.setDefaultCredentialsProvider(new PropertiesCredentialsProvider(devProperties));
                return httpClientBuilder;
            });
        }
        client = new RestHighLevelClient(builder);
    }

    RestHighLevelClient client;

    public long deleteByDataSet(String dataSet, String sharding) {
        String indexName = INDEX_NAME;
        if (StringUtils.isNotBlank(sharding)) {
            indexName = indexName + "_" + sharding;
        }
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
        request.setQuery(QueryBuilders.matchQuery("dataSet", dataSet));
        try {
            BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
            return response.getDeleted();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertTestData(TestData testData, String sharding) {
        String id = null;
        if (StringUtils.isEmpty(testData.getGroup1()) && StringUtils.isEmpty(testData.getGroup2())
            && StringUtils.isEmpty(testData.getGroup3())) {
            id = testData.getDataSet() + "_" + testData.getDataType() + "_" + testData.getOrderNo() + "_"
                + testData.getStatus();
        }

        XContentBuilder builder;
        try {
            // builder = JsonXContentEx.contentBuilder(); 有精度问题
            builder = CborXContent.contentBuilder();
            builder.startObject();
            builder.field("group1", testData.getGroup1());
            builder.field("group2", testData.getGroup2());
            builder.field("group3", testData.getGroup3());
            builder.field("dataSet", testData.getDataSet());
            builder.field("dataType", testData.getDataType());
            builder.field("orderNo", testData.getOrderNo());
            builder.field("status", testData.getStatus());
            builder.field("amount", testData.getAmount());
            builder.field("currency", testData.getCurrency());
            builder.timeField("updateTime", testData.getUpdateTime());
            builder.endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String indexName = INDEX_NAME;
        if (StringUtils.isNotBlank(sharding)) {
            indexName = indexName + "_" + sharding;
        }
        try {
            IndexRequest request;
            if (id != null) {
                request = new IndexRequest(indexName).id(id).source(builder);
            } else {
                request = new IndexRequest(indexName).source(builder);
            }
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
