package com.uaepay.pub.csc.test.dal.es;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uaepay.basis.beacon.common.util.JsonUtil;
import com.uaepay.pub.csc.domain.data.DataSourceConfig;
import com.uaepay.pub.csc.domain.data.EsDataSourceConfig;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.domainservice.data.event.DataSourceRegisteredEvent;
import com.uaepay.pub.csc.test.domain.CgsLogTestData;

@Repository
public class CgsLogTestDataEsMapper implements ApplicationListener<DataSourceRegisteredEvent> {

    @Value("${log_monitor.cgsPorterIndexPrefix:porter_cgs-}")
    public String indexName;

    RestHighLevelClient client;

    public long delete(Date beginTime, Date endTime, String sharding) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName + sharding);
        request.setQuery(QueryBuilders.rangeQuery("requestTime").from(beginTime).to(endTime).includeUpper(false));
        try {
            BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
            return response.getDeleted();
        } catch (ElasticsearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                return 0;
            }
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(CgsLogTestData testData, String sharding) {
        IndexRequest indexRequest = new IndexRequest(indexName + sharding);
        try {
            indexRequest.source(JsonUtil.toJsonString(testData), XContentType.JSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("新增文档失败", e);
        }
    }

    @Override
    public void onApplicationEvent(DataSourceRegisteredEvent dataSourceRegisteredEvent) {
        DataSourceConfigFactory dataSourceConfigFactory = dataSourceRegisteredEvent.getDataSourceConfigFactory();
        DataSourceConfig dataSourceConfig = dataSourceConfigFactory.getOrCreate("es_log_reader");
        if (dataSourceConfig instanceof EsDataSourceConfig) {
            client = ((EsDataSourceConfig)dataSourceConfig).getRestHighLevelClient();
        }
    }
}
