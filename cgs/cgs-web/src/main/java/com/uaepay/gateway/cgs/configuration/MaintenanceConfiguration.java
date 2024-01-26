package com.uaepay.gateway.cgs.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zc
 */
@Configuration
@ConfigurationProperties(prefix = "maintenance")
public class MaintenanceConfiguration {

    /** 是否开始维护 */
    private boolean enabled;

    /** 维护对外提示信息 */
    private String message;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
