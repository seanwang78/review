package com.uaepay.gateway.cgs.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sgl
 * @description
 * @date 2023-01-10 5:15 PM
 */
@AllArgsConstructor
@Data
public class AccessToken implements Serializable {
    private static final long serialVersionUID = -6042633426301646776L;
    /**
     *  第三方用户唯一标识
     */
    private String identity;

    /**
     * 注册时的账号，如：手机、邮箱ues密文
     */
    private String accountId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 应用软件名称，如：DreamIsland
     */
    private String appId;

    /**
     * accessToken
     */
    private String accessToken;
}
