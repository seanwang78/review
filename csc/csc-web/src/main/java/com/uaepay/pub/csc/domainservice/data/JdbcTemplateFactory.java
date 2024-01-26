package com.uaepay.pub.csc.domainservice.data;

import org.springframework.jdbc.core.JdbcTemplate;

import com.uaepay.pub.csc.domain.data.MySqlDataSourceConfig;

/**
 * @author zc
 */
public interface JdbcTemplateFactory {

    /**
     * 获取JdbcTemplate
     * 
     * @param dataSourceConfig
     *            数据源配置
     * @return jdbcTemplate
     * @throws Exception
     *             数据源异常时抛出
     */
    JdbcTemplate getOrCreate(MySqlDataSourceConfig dataSourceConfig);

}
