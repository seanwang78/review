package com.uaepay.gateway.cgs.app.template.ext.service;

import static com.uaepay.gateway.common.app.template.constants.LanguageConstant.DEFAULT_LANGUAGE_CONFIG;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.ObjectProvider;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.basis.beacon.common.template.AbstractCodeServiceFactory;
import com.uaepay.common.util.VelocityUtil;
import com.uaepay.gateway.cgs.app.facade.api.ClientGatewayFacade;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayResponse;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.template.domainservice.handler.ClientGatewayExceptionHandler;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.cgs.app.template.domainservice.service.ClientServiceProvider;
import com.uaepay.gateway.cgs.app.template.misc.ResponseHandler;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.app.template.common.util.GatewayJsonUtil;
import com.uaepay.gateway.common.app.template.domainservice.language.LanguageService;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

/**
 * 客户端网关门面实现
 * 
 * @author zc
 */
@Service(version = "${spring.application.name}")
public class ClientGatewayFacadeImpl extends AbstractCodeServiceFactory<ClientServiceProvider>
    implements ClientGatewayFacade {

    LanguageService languageService;
    ResponseHandler responseHandler;
    List<ClientGatewayExceptionHandler> exceptionHandlers;

    public ClientGatewayFacadeImpl(LanguageService languageService, List<ClientServiceProvider> providers,
        ResponseHandler responseHandler, ObjectProvider<ClientGatewayExceptionHandler> exceptionHandlers) {
        super(providers);
        this.languageService = languageService;
        this.responseHandler = responseHandler;
        this.exceptionHandlers = exceptionHandlers.stream().collect(toList());
        logger.info("CGS异常处理器: " + exceptionHandlers);
        // responseHandler.parseResponseClass(getResponseTypeFromProviders(providers));
    }

    /**
     * 取出所有Provider的响应类型
     * 
     * @author Zhibin Liu
     * @time 2020/2/29 15:28
     * @param providers
     */
    protected List<Type> getResponseTypeFromProviders(List<ClientServiceProvider> providers) {
        return providers.stream().filter(provider -> provider instanceof AbstractClientServiceProvider)
            .map(provider -> {
                ParameterizedType genericSuperclass = (ParameterizedType)provider.getClass().getGenericSuperclass();
                Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 2) {
                    // 取出范型的第二个参数 response
                    return actualTypeArguments[1];
                }
                return null;
            }).filter(Objects::nonNull).collect(toList());
    }

    @Override
    protected String getFactoryName() {
        return "客户端网关服务工厂";
    }

    @Override
    public String doService(ClientGatewayRequest request) {
        ClientServiceResponse<?> response = processService(request);
        return convertToResponseString(request, response);
    }

    @Override
    public ClientGatewayResponse doServiceV2(ClientGatewayRequest request) {
        ClientServiceResponse<?> response = processService(request);
        String ret = convertToResponseString(request, response);
        return new ClientGatewayResponse(ret, response != null ? response.getSessionMap() : null);
    }

    protected ClientServiceResponse<?> processService(ClientGatewayRequest request) {
        try {
            ClientServiceProvider provider = getService(request.getService());
            if (provider == null) {
                throw new GatewayFailException(GatewayReturnCode.SERVICE_NOT_AVAILABLE);
            }
            ClientServiceResponse<?> response = provider.doService(request);
            return responseHandler.handle(request, response);
        } catch (Throwable e) {
            return handleException(request, e);
        }
    }

    protected ClientServiceResponse<?> handleException(ClientGatewayRequest gatewayRequest, Throwable e) {
        ClientServiceResponse<?> response;
        if (CollectionUtils.isNotEmpty(exceptionHandlers)) {
            for (ClientGatewayExceptionHandler handler : exceptionHandlers) {
                response = handler.handle(gatewayRequest, e);
                if (response != null) {
                    return response;
                }
            }
        }
        if (e instanceof InvalidParameterException) {
            response = ClientServiceResponse.build(GatewayReturnCode.INVALID_PARAMETER, e.getMessage());
        } else if (e instanceof BizCheckFailException) {
            response = ClientServiceResponse.build(GatewayReturnCode.BIZ_CHECK_FAIL, e.getMessage());
        } else if (e instanceof GatewayFailException) {
            response = ClientServiceResponse.build(((GatewayFailException)e).getCode(), e.getMessage());
        } else if (e instanceof FailException) {
            response = ClientServiceResponse.build(GatewayReturnCode.BIZ_CHECK_FAIL, e.getMessage());
        } else if (e instanceof ErrorException) {
            logger.error("内部异常: {}" + ((ErrorException)e).getCode(), e);
            response = ClientServiceResponse.build(GatewayReturnCode.SYSTEM_ERROR);
        } else {
            logger.error("异常", e);
            response = ClientServiceResponse.build(GatewayReturnCode.SYSTEM_ERROR);
        }
        return response;
    }

    /**
     * 渲染语言文案
     */
    protected ClientServiceResponse<?> renderLanguageMessage(ClientGatewayRequest gatewayRequest,
        ClientServiceResponse<?> response) {
        try {
            String origMessage = response.getHead().getMsg();
            Map<String, Object> extParam = response.getHead().getExtParm();

            if (origMessage != null) {
                List<String> scenes = new ArrayList<>();
                if (StringUtils.isNotBlank(gatewayRequest.getLangScene())) {
                    scenes.add(gatewayRequest.getLangScene());
                }
                scenes.add("TOC");
                String message = languageService.resolveOrDefaultV2(gatewayRequest.getLangType(), origMessage, scenes,
                    DEFAULT_LANGUAGE_CONFIG);
                // String message = languageService.resolveOrDefault(gatewayRequest.getLangType(), origMessage, "TOC",
                // DEFAULT_LANGUAGE_CONFIG);
                if (extParam != null) {
                    // 额外参数不为空，则认为message为模板，处理模板
                    message = VelocityUtil.getString(message, extParam);
                }
                response.getHead().setMsg(message);
            }
            return response;
        } catch (Throwable e) {
            return handleException(gatewayRequest, e);
        }
    }

    protected String convertToResponseString(ClientGatewayRequest request, ClientServiceResponse<?> response) {
        if (response != null && response.getHead() != null) {
            response = renderLanguageMessage(request, response);
        }
        String result = GatewayJsonUtil.toJsonStringMute(response);
        return result;
    }

}
