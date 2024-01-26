package com.uaepay.pub.csc.ext.integration;

import java.util.Date;

import com.uaepay.pub.csc.datasource.facade.domain.RowDataList;
import com.uaepay.pub.csc.domain.monitor.ApiQueryConfig;

/**
 * API数据源客户端
 * 
 * @author zc
 */
public interface ApiDataSourceClient {

    /**
     * 查询数据，查询不成功即抛异常
     * 
     * @param beginTime
     *            数据开始时间
     * @param endTime
     *            数据结束时间
     * @param config
     *            查询配置
     * @return 查询结果
     */
    RowDataList queryData(Date beginTime, Date endTime, ApiQueryConfig config);

}
