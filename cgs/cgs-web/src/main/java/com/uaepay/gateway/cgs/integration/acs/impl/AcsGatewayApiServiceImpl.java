package com.uaepay.gateway.cgs.integration.acs.impl;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.uaepay.acs.service.facade.api.GatewayApiFacade;
import com.uaepay.acs.service.facade.api.domain.GatewayApiConfig;
import com.uaepay.acs.service.facade.api.domain.RateLimitGroup;
import com.uaepay.acs.service.facade.api.request.GatewayApiConfigQueryRequest;
import com.uaepay.acs.service.facade.api.request.RateLimitGroupQueryRequest;
import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.util.BooleanUtil;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.constants.CgsCacheConstants;
import com.uaepay.gateway.cgs.domain.api.ApiConfig;
import com.uaepay.gateway.cgs.domain.api.ApiVersion;
import com.uaepay.gateway.cgs.integration.BaseService;
import com.uaepay.gateway.cgs.integration.acs.AcsGatewayApiService;

/**
 * @author zc
 */
@Service
public class AcsGatewayApiServiceImpl extends BaseService implements AcsGatewayApiService {

    private static final String EXTENSION_API_TYPE = "apiType";
    private static final String EXTENSION_DEBUG = "debug";
    private static final String EXTENSION_ENCRYPT = "encrypt";
    private static final String EXTENSION_RECORD_VOUCHER = "recordVoucher";
    private static final String EXTENSION_VOUCHER_TYPE = "voucherType";
    private static final String EXTENSION_API_VERSION = "apiVersion";
    private static final String EXTENSION_SESSION_MAP = "sessionMap";
    private static final String EXTENSION_SKIP_CHECK = "skipGuardCheck";
    private static final String DEFAULT_VOUCHER_TYPE = "CGS";

    private static final String EXTENSION_RESPONSE_ENCRYPT = "responseEncrypt";
    private static final String EXTENSION_LANG_SCENE = "langScene";

    private static final String ALLOW_PLATFORM = "allowPlatform";

    @Reference
    private GatewayApiFacade gatewayApiFacade;

    @Override
    @Cacheable(value = CgsCacheConstants.ACS_GATEWAY_API_CONFIG)
    public ApiConfig getApiConfig(String apiCode) {
        GatewayApiConfigQueryRequest request = new GatewayApiConfigQueryRequest();
        request.setClientId(clientId);
        request.setGatewayType(GATEWAY_TYPE);
        request.setApiCode(apiCode);
        logger.info("acs网关API查询请求: {}", request);
        ObjectQueryResponse<GatewayApiConfig> response = gatewayApiFacade.queryApiConfig(request);
        logger.info("acs网关API查询响应: {}", response);
        if (response.getApplyStatus() != ApplyStatusEnum.SUCCESS) {
            throw new ErrorException(response.getMessage());
        }
        return convert(response.getResult());
    }

    public void getRateLimitGroup(long rateLimitGroup) {
        RateLimitGroupQueryRequest request = new RateLimitGroupQueryRequest();
        request.setClientId(clientId);
        request.setRateLimitGroup(rateLimitGroup);
        logger.info("acs限流分组查询请求: {}", request);
        ObjectQueryResponse<RateLimitGroup> response = gatewayApiFacade.queryRateLimitGroup(request);
        logger.info("acs限流分组查询响应: {}", response);
        if (response.getApplyStatus() != ApplyStatusEnum.SUCCESS) {
            throw new ErrorException(response.getMessage());
        }
    }

    private ApiConfig convert(GatewayApiConfig orig) {
        if (orig == null) {
            return null;
        }
        ApiConfig result = new ApiConfig();
        result.setApiCode(orig.getApiCode());
        result.setAppCode(orig.getAppCode());
        result.setConfigId(orig.getConfigId());
        result.setRateLimitGroup(orig.getRateLimitGroup());

        Map<String, String> extension = orig.getExtension();
        result.setApiType(parseApiType(extension));
        result.setDebug(parseDebug(extension));
        result.setEncryptConfig(parseString(orig.getExtension(), EXTENSION_ENCRYPT));
        result.setResponseEncryptConfig(parseString(orig.getExtension(), EXTENSION_RESPONSE_ENCRYPT));
        result.setRecordVoucher(parseRecordVoucher(extension));
        result.setVoucherType(parseVoucherType(extension));
        result.setApiVersion(parseApiVersion(extension));
        result.setAllowSessionMap(parseBoolean(extension, EXTENSION_SESSION_MAP));
        result.setSkipGTkCheck(parseSkipGTCheck(extension));
        result.setLangScene(parseString(orig.getExtension(), EXTENSION_LANG_SCENE));
        result.setAllowPlatform(parseString(orig.getExtension(), ALLOW_PLATFORM));
        return result;
    }

    private String parseVoucherType(Map<String, String> extension) {
        if (extension == null) {
            return DEFAULT_VOUCHER_TYPE;
        }
        return extension.getOrDefault(EXTENSION_VOUCHER_TYPE, DEFAULT_VOUCHER_TYPE);
    }

    private YesNoEnum parseRecordVoucher(Map<String, String> extension) {
        if (extension == null) {
            return YesNoEnum.NO;
        }
        String recordVoucher = extension.get(EXTENSION_RECORD_VOUCHER);
        YesNoEnum code = YesNoEnum.getByCode(recordVoucher);
        if (StringUtils.isNotBlank(recordVoucher) && code == null) {
            throw new ErrorException("API配置异常");
        }
        return ObjectUtils.defaultIfNull(code, YesNoEnum.NO);
    }

    private ApiType parseApiType(Map<String, String> extension) {
        if (extension == null) {
            return ApiType.AUTHED;
        }
        String apiType = extension.get(EXTENSION_API_TYPE);
        ApiType apiTypeEnum = ApiType.getByCode(apiType);
        if (StringUtils.isNotBlank(apiType) && apiTypeEnum == null) {
            throw new ErrorException("API配置异常");
        }
        return ObjectUtils.defaultIfNull(apiTypeEnum, ApiType.AUTHED);
    }

    private Boolean parseDebug(Map<String, String> extension) {
        if (extension == null) {
            return null;
        }
        // 为空返回true，打印日志
        if (StringUtils.isEmpty(extension.get(EXTENSION_DEBUG))) {
            return true;
        }

        return BooleanUtil.parse(extension.get(EXTENSION_DEBUG));
    }

    private String parseString(Map<String, String> extension, String key) {
        if (extension == null) {
            return null;
        }
        return extension.get(key);
    }

    private ApiVersion parseApiVersion(Map<String, String> extension) {
        if (extension == null) {
            return ApiVersion.V1;
        }
        String apiVersion = extension.get(EXTENSION_API_VERSION);
        if (StringUtils.isBlank(apiVersion)) {
            return ApiVersion.V1;
        }
        ApiVersion version = ApiVersion.getByCode(apiVersion);
        if (version == null) {
            throw new ErrorException("apiVersion配置异常");
        }
        return version;
    }

    private boolean parseSkipGTCheck(Map<String, String> extension) {
        if (extension == null) {
            return false;
        }
        return BooleanUtil.parse(extension.get(EXTENSION_SKIP_CHECK));
    }

    private boolean parseBoolean(Map<String, String> extension, String key) {
        if (extension == null) {
            return false;
        }
        return BooleanUtil.parse(extension.get(key));
    }
}
