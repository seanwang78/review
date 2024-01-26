package com.uaepay.gateway.cgs.domain.api;

import java.io.Serializable;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;

import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.app.service.common.CgsReturnCode;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author zc
 */
@Data
public class ApiConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long configId;

    private String apiCode;

    private ApiType apiType;

    private String appCode;

    private Long rateLimitGroup;

    private Boolean debug;

    private String encryptConfig;

    private String responseEncryptConfig;

    /** 检查字段配置 */
    private String checkFieldConfig;

    /** 是否记录原始凭证 */
    private YesNoEnum recordVoucher;

    /** 凭证类型 */
    private String voucherType;

    private ApiVersion apiVersion;

    /** 是否允许设置session */
    private boolean allowSessionMap;

    /** 是否跳过guardToken 检查； Y：跳过 */
    private boolean skipGTkCheck;

    /** 国际化场景，可空 */
    private String langScene;

    /**
     * 允许访问平台
     */
    private String allowPlatform;

    /**
     * 是否授权接口 true - 授权接口 false - 非授权接口
     * 
     * @return
     */
    public boolean isAuthed() {
        if (this.apiType == ApiType.AUTHED) {
            return true;
        }
        return false;
    }

    /**
     * 是否允许访问
     *
     * @param platformType
     * @return
     */
    public boolean isAllow(PlatformType platformType) {
        if(StringUtils.isBlank(allowPlatform)) {
            throw new GatewayFailException(CgsReturnCode.API_NOT_SUPPORT);
        }
        return allowPlatform.contains(platformType.getCode()) ? true : false;
    }
}