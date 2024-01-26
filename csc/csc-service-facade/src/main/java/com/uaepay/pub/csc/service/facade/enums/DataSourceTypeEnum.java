package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 数据源类型枚举
 * 
 * @author zc
 */
public enum DataSourceTypeEnum implements CodeMessageEnum {

    /** MySQL */
    MYSQL("MYSQL", "MySQL"),

    /** Mongo */
    MONGO("MONGO", "Mongo"),

    /** ES */
    ES("ES", "Elasticsearch"),

    /** API */
    API("API", "API"),

    ;

    private final String code;
    private final String message;

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static DataSourceTypeEnum getByCode(String code) {
        for (DataSourceTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    DataSourceTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
