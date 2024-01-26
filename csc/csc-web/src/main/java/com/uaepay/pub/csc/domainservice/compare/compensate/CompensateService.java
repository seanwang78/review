package com.uaepay.pub.csc.domainservice.compare.compensate;

import com.uaepay.pub.csc.domain.compare.ErrorDetail;

/**
 * 补单服务
 * 
 * @author zc
 */
public interface CompensateService {

    /**
     * 计算补单标志及通知状态
     *
     * @param errorDetail
     *            异常明细
     * @param compensateExpression
     *            补单表达式
     * @return 是否需要补单
     */
    boolean fillCompensateFlag(ErrorDetail errorDetail, String compensateExpression);

}
