package com.uaepay.gateway.cgs.domain.api;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;

/**
 * @author zc
 */
public enum ApiVersion implements CodeEnum {

    /**
     * 版本1
     */
    V1("v1"),

    /**
     * 版本2
     */
    V2("v2");

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static ApiVersion getByCode(String code) {
        for (ApiVersion type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    ApiVersion(String code) {
        this.code = code;
    }

    private final String code;

    @Override
    public String getCode() {
        return code;
    }
}
