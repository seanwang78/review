package com.uaepay.pub.csc.domainservice.data;

import java.util.List;
import java.util.Map;

import com.uaepay.pub.csc.domain.data.DataSourceConfig;
import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;

/**
 * @author zc
 */
public interface DataSourceConfigFactory {

    DataSourceConfig getOrCreate(String code);

    List<String> getCodeList();

    Map<String, List<String>> getCodeMap();

}
