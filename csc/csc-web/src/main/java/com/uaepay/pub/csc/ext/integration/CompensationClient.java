package com.uaepay.pub.csc.ext.integration;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.domain.compensate.CompensateConfig;

/**
 * 补偿客户端
 * 
 * @author zc
 */
public interface CompensationClient {

    /**
     * 补偿单个明细
     * 
     * @param detail
     *            明细
     * @param config
     *            配置
     * @return 执行结果
     */
    CommonResponse compensateSingle(CompareDetail detail, CompensateConfig config);

}
