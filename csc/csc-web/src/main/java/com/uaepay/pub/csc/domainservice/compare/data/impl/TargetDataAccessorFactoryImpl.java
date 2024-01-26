package com.uaepay.pub.csc.domainservice.compare.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.domain.data.DataSourceConfig;
import com.uaepay.pub.csc.domain.data.EsDataSourceConfig;
import com.uaepay.pub.csc.domain.data.MongoDataSourceConfig;
import com.uaepay.pub.csc.domain.data.MySqlDataSourceConfig;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessor;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessorFactory;
import com.uaepay.pub.csc.domainservice.compare.data.es.EsTargetDataAccessorFactory;
import com.uaepay.pub.csc.domainservice.compare.data.mongo.MongoTargetDataAccessorFactory;
import com.uaepay.pub.csc.domainservice.compare.data.mysql.MysqlTargetDataAccessorFactory;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;

/**
 * @author zc
 */
@Service
public class TargetDataAccessorFactoryImpl implements TargetDataAccessorFactory {

    @Autowired
    private DataSourceConfigFactory dataSourceConfigFactory;

    @Autowired
    private MysqlTargetDataAccessorFactory mysqlTargetDataAccessorFactory;

    @Autowired
    private MongoTargetDataAccessorFactory mongoTargetDataAccessorFactory;

    @Autowired
    private EsTargetDataAccessorFactory esTargetDataAccessorFactory;

    @Override
    public TargetDataAccessor create(String datasourceCode, String sqlTemplate, String relateField,
        String shardingExpression) {
        // 获取数据源配置
        DataSourceConfig dataSourceConfig = dataSourceConfigFactory.getOrCreate(datasourceCode);
        if (dataSourceConfig == null) {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "datasource not exist: " + datasourceCode);
        }
        if (dataSourceConfig instanceof MySqlDataSourceConfig) {
            return mysqlTargetDataAccessorFactory.create((MySqlDataSourceConfig)dataSourceConfig, sqlTemplate,
                relateField, shardingExpression);
        } else if (dataSourceConfig instanceof MongoDataSourceConfig) {
            return mongoTargetDataAccessorFactory.create((MongoDataSourceConfig)dataSourceConfig, sqlTemplate,
                relateField, shardingExpression);
        } else if (dataSourceConfig instanceof EsDataSourceConfig) {
            return esTargetDataAccessorFactory.create((EsDataSourceConfig)dataSourceConfig, sqlTemplate, relateField,
                shardingExpression);
        } else {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "data source type not supported");
        }
    }

}
