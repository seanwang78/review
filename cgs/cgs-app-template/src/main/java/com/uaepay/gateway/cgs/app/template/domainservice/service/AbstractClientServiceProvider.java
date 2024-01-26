package com.uaepay.gateway.cgs.app.template.domainservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.template.constants.MessageConstants;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.app.template.common.util.GatewayJsonUtil;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

/**
 * 服务提供抽象
 * 
 * @param <ReqBody>
 *            请求body类
 * @param <RespBody>
 *            响应body的类
 */
public abstract class AbstractClientServiceProvider<ReqBody, RespBody> implements ClientServiceProvider {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ClientServiceResponse<?> doService(ClientGatewayRequest request) {
        if (request.getApiType() != getApiType()) {
            throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR, "API类型不匹配");
        }
        Class<ReqBody> bodyType = getReqBodyClazz();
        ReqBody body = null;
        if (bodyType != null) {
            try {
                body = GatewayJsonUtil.parseObject(request.getBody(), bodyType);
            } catch (InvalidFormatException e) {
                ParameterValidate.invalidParameterValue(e.getValue() + "");
            } catch (Throwable e) {
                throw new InvalidParameterException(MessageConstants.REQUEST_BODY_FORMAT_ERROR);
            }
        }
        ClientServiceRequest<ReqBody> serviceRequest = buildServiceRequest(request, body);
        processValidate(serviceRequest);
        return processService(serviceRequest);
    }

    /**
     * 返回用于校验的API类型，防止配置错误
     * 
     * @return API类型
     */
    protected abstract ApiType getApiType();

    /**
     * 返回业务参数对象类型，用于统一反序列化
     * 
     * @return 业务参数对象类型，如果无参数返回空
     */
    protected abstract Class<ReqBody> getReqBodyClazz();

    /**
     * 执行校验
     * 
     * @param request
     *            服务请求
     * @throws GatewayFailException
     *             申请失败的异常
     */
    protected abstract void processValidate(ClientServiceRequest<ReqBody> request) throws GatewayFailException;

    /**
     * 执行服务
     * 
     * @param request
     *            服务请求
     * @return 服务响应
     * @throws GatewayFailException
     *             * 申请失败的异常
     */
    protected abstract ClientServiceResponse<RespBody> processService(ClientServiceRequest<ReqBody> request)
        throws GatewayFailException;

    private ClientServiceRequest<ReqBody> buildServiceRequest(ClientGatewayRequest gatewayRequest, ReqBody body) {
        ClientServiceRequest<ReqBody> request = new ClientServiceRequest<>();
        request.setLangType(gatewayRequest.getLangType());
        request.setPlatform(gatewayRequest.getPlatform());
        request.setAccessMember(gatewayRequest.getAccessMember());
        request.setAccessToken(gatewayRequest.getAccessToken());
        request.setBody(body);
        request.setHeader(gatewayRequest.getHeader());
        request.setLangScene(gatewayRequest.getLangScene());
        return request;
    }

}
