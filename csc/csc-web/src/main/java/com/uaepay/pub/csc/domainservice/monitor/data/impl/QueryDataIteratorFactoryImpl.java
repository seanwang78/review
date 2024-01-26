package com.uaepay.pub.csc.domainservice.monitor.data.impl;

import com.uaepay.pub.csc.domainservice.monitor.data.api.ApiQueryDataIteratorFactory;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.monitor.data.es.EsQueryDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.monitor.data.mongo.MongoQueryDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.monitor.data.mysql.MysqlQueryDataIteratorFactory;

import lombok.AllArgsConstructor;

/**
 * @author zc
 */
@AllArgsConstructor
@Service
public class QueryDataIteratorFactoryImpl implements QueryDataIteratorFactory {

    private DataSourceConfigFactory dataSourceConfigFactory;
    private MysqlQueryDataIteratorFactory mysqlQueryDataIteratorFactory;
    private MongoQueryDataIteratorFactory mongoQueryDataIteratorFactory;
    private EsQueryDataIteratorFactory esQueryDataIteratorFactory;
    private ApiQueryDataIteratorFactory apiQueryDataIteratorFactory;

    @Override
    public QueryDataIterator create(MonitorDefine define, QueryParam queryParam) {
        // 获取数据源配置
        DataSourceConfig dataSourceConfig = dataSourceConfigFactory.getOrCreate(define.getDatasourceCode());
        if (dataSourceConfig == null) {
            throw new FailException(CommonReturnCode.CONFIG_ERROR,
                "datasource not exist: " + define.getDatasourceCode());
        }
        if (dataSourceConfig instanceof MySqlDataSourceConfig) {
            return mysqlQueryDataIteratorFactory.create((MySqlDataSourceConfig)dataSourceConfig, define, queryParam);
        } else if (dataSourceConfig instanceof MongoDataSourceConfig) {
            return mongoQueryDataIteratorFactory.create((MongoDataSourceConfig)dataSourceConfig, define, queryParam);
        } else if (dataSourceConfig instanceof EsDataSourceConfig) {
            return esQueryDataIteratorFactory.create((EsDataSourceConfig)dataSourceConfig, define, queryParam);
        } else if (dataSourceConfig instanceof ApiDataSourceConfig) {
            return apiQueryDataIteratorFactory.create(define, queryParam);
        } else {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "data source type not supported");
        }
    }

}
