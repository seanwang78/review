package com.uaepay.gateway.cgs.domainservice;

import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.voucher.facade.response.ReqVoucherResponse;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2019/11/24
 * @since 0.1
 */
public interface VoucherService {


    /**
     * 记录请求凭证
     * @author Zhibin Liu
     * @time 2019/11/25 17:20
     * @param context
     */
    ReqVoucherResponse recordReqVoucher(ReceiveOrderContext context);

    /**
     * 记录响应凭证
     * @author Zhibin Liu
     * @time 2019/11/25 17:21
     * @param context
     * @param response
     */
    void recordResponseVoucher(ReceiveOrderContext context, String response);
}
