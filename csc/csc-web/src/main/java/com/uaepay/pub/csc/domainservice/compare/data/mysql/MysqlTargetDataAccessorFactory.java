package com.uaepay.pub.csc.domainservice.compare.data.mysql;

import java.util.List;

import com.uaepay.pub.csc.domainservice.data.mysql.MysqlSqlTemplateReplacer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.MySqlDataSourceConfig;
import com.uaepay.pub.csc.domain.data.RowDataConverter;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.data.JdbcTemplateFactory;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessor;

/**
 * @author zc
 */
@Service
public class MysqlTargetDataAccessorFactory {

    @Autowired
    private JdbcTemplateFactory jdbcTemplateFactory;

    @Autowired
    private CompareProperties compareProperties;

    public TargetDataAccessor create(MySqlDataSourceConfig dataSourceConfig, String sqlTemplate, String relateField,
        String shardingExpression) {
        MysqlTargetDataAccessor accessor =
            new MysqlTargetDataAccessor(dataSourceConfig, sqlTemplate, relateField, shardingExpression);
        accessor.init();
        return accessor;
    }

    public class MysqlTargetDataAccessor implements TargetDataAccessor {

        public MysqlTargetDataAccessor(MySqlDataSourceConfig dataSourceConfig, String sqlTemplate, String relateField,
            String shardingExpression) {
            this.dataSourceConfig = dataSourceConfig;
            this.sqlTemplate = sqlTemplate;
            this.relateField = relateField;
            this.shardingExpression = shardingExpression;
        }

        MySqlDataSourceConfig dataSourceConfig;
        String sqlTemplate;
        String relateField;
        String shardingExpression;

        JdbcTemplate jdbcTemplate;

        public void init() {
            // 验证sql
            ParameterValidate.assertTrue("{id_s} or {id_n} not exist",
                SqlTemplateParamEnum.ID_NUMBER.find(sqlTemplate) || SqlTemplateParamEnum.ID_STRING.find(sqlTemplate));

            // 获取数据库操作模版
            jdbcTemplate = jdbcTemplateFactory.getOrCreate(dataSourceConfig);
        }

        @Override
        public TargetRows queryTargetRows(List<String> relateValues) {
            String sql = buildSql(relateValues);
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
            ColumnData columnData = ColumnData.from(rowSet.getMetaData(), relateField);
            return new TargetRows(RowDataConverter.from(rowSet, columnData));
        }

        private String buildSql(List<String> relateValues) {
            MysqlSqlTemplateReplacer replacer = new MysqlSqlTemplateReplacer(sqlTemplate);
            if (SqlTemplateParamEnum.ID_NUMBER.find(sqlTemplate)) {
                replacer.replaceNumberList(SqlTemplateParamEnum.ID_NUMBER, relateValues);
            }
            if (SqlTemplateParamEnum.ID_STRING.find(sqlTemplate)) {
                replacer.replaceStringList(SqlTemplateParamEnum.ID_STRING, relateValues);
            }
            return replacer.getSql();
        }

    }

}
