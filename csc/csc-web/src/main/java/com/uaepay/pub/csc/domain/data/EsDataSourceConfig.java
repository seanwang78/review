package com.uaepay.pub.csc.domain.data;

import org.elasticsearch.client.RestHighLevelClient;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * elasticsearch数据源配置
 * 
 * @author zc
 */
@Data
@AllArgsConstructor
public class EsDataSourceConfig implements DataSourceConfig {

    String code;

    RestHighLevelClient restHighLevelClient;

}
