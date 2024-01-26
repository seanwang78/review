package com.uaepay.gateway.cgs.app.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;

/**
 * 平台类型
 */
public enum PlatformType implements CodeEnum {

    /**  */
    iOS("1"), Android("2"), Web("3"),H5("4"),MINIAPP("5")
    ,MINIAPP_BOITM_PAY("6")

    ;

    private final String code;

    /**
     * 根据编码获取枚举
     * 
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static PlatformType getByCode(String code) {
        for (PlatformType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    PlatformType(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
