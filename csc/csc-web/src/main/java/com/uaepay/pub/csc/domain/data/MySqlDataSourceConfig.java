package com.uaepay.pub.csc.domain.data;

import javax.sql.DataSource;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * mysql数据源配置
 * 
 * @author zc
 */
@Data
@AllArgsConstructor
public class MySqlDataSourceConfig implements DataSourceConfig {

    String code;

    DataSource dataSource;

}
