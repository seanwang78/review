package com.uaepay.gateway.cgs.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.domain.api.ApiConfig;
import com.uaepay.gateway.cgs.domainservice.VoucherService;
import com.uaepay.voucher.facade.api.VoucherFacade;
import com.uaepay.voucher.facade.request.ReqVoucherRequest;
import com.uaepay.voucher.facade.request.ResultVoucherRequest;
import com.uaepay.voucher.facade.response.ReqVoucherResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Function;

import static com.uaepay.gateway.cgs.constants.GatewayConstants.TRACE_ID;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2019/11/24
 * @since 0.1
 */
@Slf4j
@Service
public class VoucherServiceImpl implements VoucherService {
    @Reference
    private VoucherFacade voucherFacade;

    @Override
    public ReqVoucherResponse recordReqVoucher(ReceiveOrderContext context) {
        try {
            ApiConfig apiConfig = context.getApiConfig();

            ReqVoucherRequest request = new ReqVoucherRequest();
            request.setVoucherType(apiConfig.getVoucherType());
            request.setBodyInfo(context.getBody());
            request.setHeadInfo(JSON.toJSONString(extraReqHeaders(context)));

            ReqVoucherResponse response = voucherFacade.recordReqVoucher(request);
            log.info("[VoucherService->recordVoucher->response = {} ]", JSON.toJSONString(response));
            return response;
        } catch (Throwable e) {
            log.error("[VoucherService->recordVoucher->exception 报错]", e);
            return null;
        }
    }

    @Override
    public void recordResponseVoucher(ReceiveOrderContext context, String response) {
        try {
            ApiConfig apiConfig = context.getApiConfig();

            ResultVoucherRequest request = new ResultVoucherRequest();
            request.setVoucherType(apiConfig.getVoucherType());
            request.setVoucherNo(context.getHeader().getReqVoucherNo());
            request.setBodyInfo(response);
            request.setHeadInfo(JSON.toJSONString(extraResHeaders(context)));

            voucherFacade.recordResultVoucher(request);
            log.info("[VoucherService->recordVoucher->response = {} ]", JSON.toJSONString(response));

        } catch (Throwable e) {
            log.error("[VoucherService->recordVoucher->exception 报错]", e);
        }
    }

    private Map<String, String> extraReqHeaders(ReceiveOrderContext context) {
        HttpServletRequest httpRequest = context.getHttpRequest();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        return buildMap(headerNames, t -> httpRequest.getHeader(t));
    }

    private Map<String, String> extraResHeaders(ReceiveOrderContext context) {
        HttpServletResponse response = context.getHttpResponse();
        Collection<String> headerNames = response.getHeaderNames();
        return Maps.toMap(headerNames, t -> response.getHeader(t));
    }

    public Map<String, String> buildMap(Enumeration<String> enumeration, Function<String, String> function) {
        Map<String, String> headers = Maps.newHashMap();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = function.apply(name);
            headers.put(name, value);
        }
        String traceId = TraceContext.traceId();
        if (StringUtils.isBlank(traceId)) {
            headers.put(TRACE_ID, traceId);
        }
        return headers;
    }

}
