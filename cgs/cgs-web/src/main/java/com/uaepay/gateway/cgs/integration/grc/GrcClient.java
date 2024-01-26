package com.uaepay.gateway.cgs.integration.grc;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.grc.connect.api.stub.SearchStub;
import com.uaepay.grc.connect.api.vo.domain.OtherEventInfo;
import com.uaepay.grc.connect.api.vo.request.QueryOtherEventRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * .
 * <p>
 *
 * @author yusai
 * @date 2021-06-17 15:51.
 */
@Slf4j
@Component
public class GrcClient {

    @Value("${spring.application.name}")
    private String clientId;

    @Reference
    private SearchStub searchStub;

    public OtherEventInfo querySingleOtherEvent(String eventId, String ticket) {

        QueryOtherEventRequest request = new QueryOtherEventRequest();
        request.setClientId(clientId);
        request.setEventId(eventId);
        request.setTicket(ticket);

        ObjectQueryResponse<OtherEventInfo> response = null;
        try {
            log.info("cgs -> grc queryOtherEvent request:{}", request);
            response = searchStub.queryOtherEvent(request);
            log.info("cgs -> grc queryOtherEvent response:{}", response);
        } catch (Throwable e) {
            log.error("查询非支付详情异常:", e);
            throw new ErrorException(response.getMessage());
        }
        if (response != null && ApplyStatusEnum.SUCCESS == response.getApplyStatus()) {
            return response.getResult();
        }
        return null;
    }

}
