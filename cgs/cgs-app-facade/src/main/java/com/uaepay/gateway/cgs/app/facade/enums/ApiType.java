package com.uaepay.gateway.cgs.app.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;

/**
 * API类型
 * 
 * @author zc
 */
public enum ApiType implements CodeEnum {

    /**
     * 未授权
     */
    UNAUTHED("U"),

    /**
     * 已授权
     */
    AUTHED("A"),

    /**
     * 当ApiType配置成这个参数时，将不是明确表明改接口是 "已授权" 还是 "未授权"
     *
     * 1、接口可以传token或者不传token
     * 2、如果传了token会进行验签，并且查询accessMember数据
     * 3、如果没传token则不回进行验签，直接放行，并且没有accessMember数据
     * 4、如果是H5则会判断当前session中有没有绑定accessToken，如果有绑定accessToken则会进行accessMember解析
     *
     * 通过该接口加盐数据 与非授权的情况获取盐值一致
     */
    CONDITION("C")

    ;

    private final String code;

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static ApiType getByCode(String code) {
        for (ApiType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    ApiType(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
