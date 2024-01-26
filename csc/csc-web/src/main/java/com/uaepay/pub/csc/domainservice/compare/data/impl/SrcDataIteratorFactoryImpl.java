package com.uaepay.pub.csc.domainservice.compare.data.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIterator;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.compare.data.es.EsSrcDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.compare.data.mongo.MongoSrcDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.compare.data.mysql.MysqlSrcDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;

/**
 * @author zc
 */
@Service
public class SrcDataIteratorFactoryImpl implements SrcDataIteratorFactory {

    @Autowired
    private DataSourceConfigFactory dataSourceConfigFactory;

    @Autowired
    private MysqlSrcDataIteratorFactory mysqlSrcDataIteratorFactory;

    @Autowired
    private MongoSrcDataIteratorFactory mongoSrcDataIteratorFactory;

    @Autowired
    private EsSrcDataIteratorFactory esSrcDataIteratorFactory;

    @Override
    public SrcDataIterator create(String datasourceCode, String sqlTemplate, int splitMinutes, String relateField,
        QueryParam queryParam) {
        // 获取数据源配置
        DataSourceConfig dataSourceConfig = dataSourceConfigFactory.getOrCreate(datasourceCode);
        if (dataSourceConfig == null) {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "datasource not exist: " + datasourceCode);
        }
        if (dataSourceConfig instanceof MySqlDataSourceConfig) {
            return mysqlSrcDataIteratorFactory.create((MySqlDataSourceConfig)dataSourceConfig, sqlTemplate,
                splitMinutes, relateField, queryParam);
        } else if (dataSourceConfig instanceof MongoDataSourceConfig) {
            return mongoSrcDataIteratorFactory.create((MongoDataSourceConfig)dataSourceConfig, sqlTemplate,
                splitMinutes, relateField, queryParam);
        } else if (dataSourceConfig instanceof EsDataSourceConfig) {
            return esSrcDataIteratorFactory.create((EsDataSourceConfig)dataSourceConfig, sqlTemplate, splitMinutes,
                relateField, queryParam);
        } else {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "data source type not supported");
        }
    }

}
