package com.uaepay.pub.csc.domainservice.compare.data.mongo;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.pub.csc.core.common.util.MongoUtil;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domain.data.MongoDataSourceConfig;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessor;
import com.uaepay.pub.csc.domainservice.data.MongoTemplateFactory;
import com.uaepay.pub.csc.domainservice.data.mongo.MongoSqlTemplateReplacer;

/**
 * @author zc
 */
@Service
public class MongoTargetDataAccessorFactory {

    @Autowired
    private MongoTemplateFactory mongoTemplateFactory;

    @Autowired
    private CompareProperties compareProperties;

    public TargetDataAccessor create(MongoDataSourceConfig dataSourceConfig, String sqlTemplate, String relateField,
        String shardingExpression) {
        MongoTargetDataAccessor accessor =
            new MongoTargetDataAccessor(dataSourceConfig, sqlTemplate, relateField, shardingExpression);
        accessor.init();
        return accessor;
    }

    public class MongoTargetDataAccessor implements TargetDataAccessor {

        public MongoTargetDataAccessor(MongoDataSourceConfig dataSourceConfig, String sqlTemplate, String relateField,
            String shardingExpression) {
            this.dataSourceConfig = dataSourceConfig;
            this.sqlTemplate = sqlTemplate;
            this.relateField = relateField;
            this.shardingExpression = shardingExpression;
        }

        MongoDataSourceConfig dataSourceConfig;
        String sqlTemplate;
        String relateField;
        String shardingExpression;

        MongoTemplate mongoTemplate;

        public void init() {
            // 验证sql
            ParameterValidate.assertTrue("{id_s} or {id_n} not exist",
                SqlTemplateParamEnum.ID_NUMBER.find(sqlTemplate) || SqlTemplateParamEnum.ID_STRING.find(sqlTemplate));

            // 替换特殊占位符，否则解析会报错
            String tempTemplate =
                new MongoSqlTemplateReplacer(sqlTemplate).replaceNumberList(SqlTemplateParamEnum.ID_NUMBER, null)
                    .replaceStringList(SqlTemplateParamEnum.ID_STRING, null).getSql();
            String databaseName = MongoUtil.extractDatabaseName(tempTemplate, "target sql");
            // 获取数据库操作模版
            mongoTemplate = mongoTemplateFactory.getOrCreate(dataSourceConfig, databaseName);
        }

        @Override
        public TargetRows queryTargetRows(List<String> relateValues) {
            Document query = buildQuery(relateValues);
            List<RowData> rows = MongoUtil.execute(mongoTemplate, query, relateField);
            return new TargetRows(rows);
        }

        private Document buildQuery(List<String> relateValues) {
            MongoSqlTemplateReplacer replacer = new MongoSqlTemplateReplacer(sqlTemplate);
            if (SqlTemplateParamEnum.ID_NUMBER.find(sqlTemplate)) {
                replacer.replaceNumberList(SqlTemplateParamEnum.ID_NUMBER, relateValues);
            }
            if (SqlTemplateParamEnum.ID_STRING.find(sqlTemplate)) {
                replacer.replaceStringList(SqlTemplateParamEnum.ID_STRING, relateValues);
            }
            Document query = Document.parse(replacer.getSql());
            query.remove(MongoUtil.DB);
            MongoUtil.appendLimit(query, compareProperties.getQueryTargetSize());
            return query;
        }

    }

}
