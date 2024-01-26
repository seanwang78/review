package com.uaepay.pub.csc.datasource.facade;

import com.uaepay.pub.csc.datasource.facade.request.QueryDataRequest;
import com.uaepay.pub.csc.datasource.facade.response.QueryDataResponse;

/**
 * API数据源接口，由应用实现
 * <li>目前只支持SQL监控使用</li>
 * 
 * @author zc
 */
public interface ApiDataSourceFacade {

    /**
     * 查询数据
     * 
     * @param request
     *            查询请求
     * @return 查询结果，没有数据时返回APPLY_SUCCESS，其他申请结果会导致监控查询异常
     */
    QueryDataResponse queryData(QueryDataRequest request);

}
