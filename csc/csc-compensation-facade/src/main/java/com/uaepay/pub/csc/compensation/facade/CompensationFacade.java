package com.uaepay.pub.csc.compensation.facade;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.compensation.facade.request.CompensateSingleRequest;

/**
 * 补单接口
 * 
 * @author zc
 */
public interface CompensationFacade {

    /**
     * 申请单笔补单
     * 
     * @param request
     *            补单请求
     * @return 申请结果
     */
    CommonResponse applySingle(CompensateSingleRequest request);

}
