package com.uaepay.pub.csc.domainservice.monitor.data.mysql;

import java.util.Iterator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.monitor.QueryRows;
import com.uaepay.pub.csc.domain.properties.MonitorProperties;
import com.uaepay.pub.csc.domainservice.data.JdbcTemplateFactory;
import com.uaepay.pub.csc.domainservice.data.QueryParamSplitter;
import com.uaepay.pub.csc.domainservice.data.mysql.MysqlSqlTemplateReplacer;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.SplitStrategyEnum;

import lombok.Data;

/**
 * @author zc
 */
@Service
public class MysqlQueryDataIteratorFactory {

    public MysqlQueryDataIteratorFactory(JdbcTemplateFactory jdbcTemplateFactory, MonitorProperties monitorProperties) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
        this.monitorProperties = monitorProperties;
    }

    private JdbcTemplateFactory jdbcTemplateFactory;

    private MonitorProperties monitorProperties;

    public QueryDataIterator create(MySqlDataSourceConfig dataSourceConfig, MonitorDefine define,
        QueryParam queryParam) {
        JdbcTemplate jdbcTemplate = jdbcTemplateFactory.getOrCreate(dataSourceConfig);
        return new MysqlQueryDataIterator(jdbcTemplate, queryParam, define);
    }

    @Data
    public class MysqlQueryDataIterator implements QueryDataIterator {

        public MysqlQueryDataIterator(JdbcTemplate jdbcTemplate, QueryParam queryParam, MonitorDefine define) {
            this.jdbcTemplate = jdbcTemplate;
            if (define.getSplitStrategy() == SplitStrategyEnum.NO) {
                this.splitParamsIterator = QueryParamSplitter.splitQueryParamByTime(queryParam, 0).iterator();
            } else {
                this.splitParamsIterator =
                    QueryParamSplitter.splitQueryParamByTime(queryParam, define.getSplitMinutes()).iterator();
            }

            this.queryTemplate = define.getQueryTemplate();
            this.monitorType = define.getMonitorType();
            this.keyField = define.getKeyField();
            this.splitStrategy = define.getSplitStrategy();
            validate();
        }

        JdbcTemplate jdbcTemplate;
        Iterator<QuerySplitParam> splitParamsIterator;
        String queryTemplate;
        MonitorTypeEnum monitorType;
        String keyField;
        SplitStrategyEnum splitStrategy;

        protected void validate() {
            ParameterValidate.assertTrue("{begin} not exist", SqlTemplateParamEnum.BEGIN_TIME.find(queryTemplate));
            ParameterValidate.assertTrue("{end} not exist", SqlTemplateParamEnum.END_TIME.find(queryTemplate));
            ParameterValidate.assertTrue("{count} not exist", SqlTemplateParamEnum.COUNT.find(queryTemplate));
        }

        @Override
        public boolean hasNext() {
            return splitParamsIterator != null && splitParamsIterator.hasNext();
        }

        @Override
        public QueryRows next() {
            String sql = buildSql(splitParamsIterator.next());
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
            ColumnData columnData = ColumnData.from(rowSet.getMetaData(), keyField, false);
            return new QueryRows(RowDataConverter.from(rowSet, columnData));
        }

        private String buildSql(QuerySplitParam querySplitParam) {
            return new MysqlSqlTemplateReplacer(queryTemplate)
                .replaceDate(SqlTemplateParamEnum.BEGIN_TIME, querySplitParam.getBeginTime())
                .replaceDate(SqlTemplateParamEnum.END_TIME, querySplitParam.getEndTime())
                .replaceNumber(SqlTemplateParamEnum.COUNT, monitorProperties.getMaxDetailCount(monitorType) + 1)
                .getSql();
        }

    }

}
