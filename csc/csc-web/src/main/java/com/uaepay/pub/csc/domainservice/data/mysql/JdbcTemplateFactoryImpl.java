package com.uaepay.pub.csc.domainservice.data.mysql;

import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.domain.data.MySqlDataSourceConfig;
import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.data.JdbcTemplateFactory;

/**
 * @author zc
 */
@Slf4j
@Service
public class JdbcTemplateFactoryImpl implements JdbcTemplateFactory {

    private static final String CHECK_SQL = "select 'csc'";

    @Autowired
    private CompareProperties compareProperties;

    private final ConcurrentHashMap<String, JdbcTemplate> dataSourceMap = new ConcurrentHashMap<>();

    @Override
    public JdbcTemplate getOrCreate(MySqlDataSourceConfig dataSourceConfig) {
        String key = dataSourceConfig.getCode();
        JdbcTemplate result = dataSourceMap.get(key);
        if (result != null) {
            return result;
        }
        synchronized (dataSourceMap) {
            result = dataSourceMap.get(key);
            if (result != null) {
                return result;
            }
            result = build(dataSourceConfig);
            dataSourceMap.put(key, result);
        }
        return result;
    }

    private JdbcTemplate build(MySqlDataSourceConfig dataSourceConfig) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourceConfig.getDataSource());
        jdbcTemplate.setQueryTimeout(compareProperties.getQueryTimeOut());
        jdbcTemplate.execute(CHECK_SQL);
        log.info("测试连接成功: {}", dataSourceConfig.getCode());
        return jdbcTemplate;
    }

}
