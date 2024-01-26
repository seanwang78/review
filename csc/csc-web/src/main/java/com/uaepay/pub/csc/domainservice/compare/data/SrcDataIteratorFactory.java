package com.uaepay.pub.csc.domainservice.compare.data;

import com.uaepay.pub.csc.domain.data.QueryParam;

/**
 * 源数据迭代器工厂
 * 
 * @author zc
 */
public interface SrcDataIteratorFactory {

    SrcDataIterator create(String datasourceCode, String sqlTemplate, int splitMinutes, String relateField,
        QueryParam queryParam);

}
