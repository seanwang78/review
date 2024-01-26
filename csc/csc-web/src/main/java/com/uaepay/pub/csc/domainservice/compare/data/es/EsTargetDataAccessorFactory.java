package com.uaepay.pub.csc.domainservice.compare.data.es;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.LoggingDeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.core.common.util.EsUtil;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.EsDataSourceConfig;
import com.uaepay.pub.csc.domain.data.RowDataConverter;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessor;
import com.uaepay.pub.csc.domainservice.data.es.EsSqlTemplateReplacer;
import com.uaepay.pub.csc.domainservice.data.mongo.MongoSqlTemplateReplacer;

/**
 * @author zc
 */
@Service
public class EsTargetDataAccessorFactory {

    @Autowired
    private CompareProperties compareProperties;

    public TargetDataAccessor create(EsDataSourceConfig dataSourceConfig, String sqlTemplate, String relateField,
        String shardingExpression) {
        EsTargetDataAccessor accessor =
            new EsTargetDataAccessor(dataSourceConfig, sqlTemplate, relateField, shardingExpression);
        accessor.init();
        return accessor;
    }

    public class EsTargetDataAccessor implements TargetDataAccessor {

        public EsTargetDataAccessor(EsDataSourceConfig dataSourceConfig, String sqlTemplate, String relateField,
            String shardingExpression) {
            this.restHighLevelClient = dataSourceConfig.getRestHighLevelClient();
            this.sqlTemplate = sqlTemplate;
            this.relateField = relateField;
            this.shardingExpression = shardingExpression;
        }

        RestHighLevelClient restHighLevelClient;
        String sqlTemplate;
        String relateField;
        String shardingExpression;
        String indexName;

        public void init() {
            // 验证sql
            ParameterValidate.assertTrue("{id_s} or {id_n} not exist",
                SqlTemplateParamEnum.ID_NUMBER.find(sqlTemplate) || SqlTemplateParamEnum.ID_STRING.find(sqlTemplate));

            // 替换特殊占位符，否则解析会报错
            String tempTemplate =
                new MongoSqlTemplateReplacer(sqlTemplate).replaceNumberList(SqlTemplateParamEnum.ID_NUMBER, null)
                    .replaceStringList(SqlTemplateParamEnum.ID_STRING, null).getSql();
            indexName = EsUtil.extractIndexName(tempTemplate, "target sql");
        }

        @Override
        public TargetRows queryTargetRows(List<String> relateValues) {
            SearchRequest query = buildQuery(relateValues);
            SearchHit[] batch = EsUtil.processSearch(restHighLevelClient, query);
            ColumnData columnData = ColumnData.fromEs(batch, relateField);
            return new TargetRows(RowDataConverter.fromEs(batch, columnData));
        }

        private SearchRequest buildQuery(List<String> relateValues) {
            // 准备参数
            EsSqlTemplateReplacer replacer = new EsSqlTemplateReplacer(sqlTemplate);
            if (SqlTemplateParamEnum.ID_NUMBER.find(sqlTemplate)) {
                replacer.replaceNumberList(SqlTemplateParamEnum.ID_NUMBER, relateValues);
            }
            if (SqlTemplateParamEnum.ID_STRING.find(sqlTemplate)) {
                replacer.replaceStringList(SqlTemplateParamEnum.ID_STRING, relateValues);
            }
            String template = replacer.getSql();
            JSONObject sqlObject = JSON.parseObject(template);
            sqlObject.remove(EsUtil.INDEX);
            JSONObject queryObject = (JSONObject)sqlObject.remove("query");

            // 构造search请求
            SearchRequest request = new SearchRequest(indexName);
            try {
                XContentParser jsonParser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
                    LoggingDeprecationHandler.INSTANCE, sqlObject.toJSONString());
                SearchSourceBuilder sourceBuilder = SearchSourceBuilder.fromXContent(jsonParser);
                sourceBuilder.query(QueryBuilders.wrapperQuery(queryObject.toJSONString()));
                sourceBuilder.size(compareProperties.getQueryTargetSize());
                request.source(sourceBuilder);
            } catch (IOException e) {
                throw new FailException(CommonReturnCode.CONFIG_ERROR, "build target query fail: " + e.getMessage());
            }
            return request;
        }

    }

}
