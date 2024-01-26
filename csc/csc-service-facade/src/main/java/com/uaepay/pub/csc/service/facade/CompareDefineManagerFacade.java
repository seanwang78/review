package com.uaepay.pub.csc.service.facade;

import java.util.List;
import java.util.Map;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;

/**
 * 对账定义管理服务
 * 
 * @author zc
 */
public interface CompareDefineManagerFacade {

    /**
     * 获取支持的数据源代码列表
     * 
     * @return 数据源代码列表
     */
    @Deprecated
    ObjectQueryResponse<List<String>> getDataSourceCodeList(OperateRequest request);

    /**
     * 获取支持的数据源代码列表
     * 
     * @param request
     *            操作请求
     * @return 数据源代码map, key参考DataSourceTypeEnum
     * @see DataSourceTypeEnum
     */
    ObjectQueryResponse<Map<String, List<String>>> getDataSourceMap(OperateRequest request);

}
