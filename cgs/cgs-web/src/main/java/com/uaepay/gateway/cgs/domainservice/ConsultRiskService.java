package com.uaepay.gateway.cgs.domainservice;

import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;

/**
 * @Description: 风控接口
 * @author heyang
 * @date Dec 26, 2019 2:11:57 PM
 */
public interface ConsultRiskService {

    /**
	 * 风控埋点校验
     * @param context
     * @return void
     */
    void riskBuriedPoint(ReceiveOrderContext context);

}
