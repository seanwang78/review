package com.uaepay.pub.csc.domain.data;

import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;

/**
 * API数据源
 * 
 * @author zc
 */
public class ApiDataSourceConfig implements DataSourceConfig {

    @Override
    public String getCode() {
        return DataSourceTypeEnum.API.getCode();
    }

}
