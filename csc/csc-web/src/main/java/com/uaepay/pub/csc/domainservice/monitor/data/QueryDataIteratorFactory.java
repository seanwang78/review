package com.uaepay.pub.csc.domainservice.monitor.data;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.domain.data.QueryParam;

/**
 * 数据查询迭代器工厂
 * 
 * @author zc
 */
public interface QueryDataIteratorFactory {

    /**
     * 创建一个用于迭代查询数据的处理器
     * 
     * @param define 定义
     * @param queryParam 查询参数
     * @return
     */
    QueryDataIterator create(MonitorDefine define, QueryParam queryParam);

}
