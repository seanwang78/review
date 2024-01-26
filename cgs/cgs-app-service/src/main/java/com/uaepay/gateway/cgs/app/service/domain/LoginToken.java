package com.uaepay.gateway.cgs.app.service.domain;

import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;

import lombok.Data;

@Data
public class LoginToken {

    public LoginToken(KeyInfo keyInfo, AccessMember accessMember) {
        this.keyInfo = keyInfo;
        this.accessMember = accessMember;
    }

    /** 登录唯一键，已存的会强制登出 */
    private KeyInfo keyInfo;

    /** 会员信息 */
    private AccessMember accessMember;

    @Data
    public static class KeyInfo {

        public KeyInfo() {}

        public KeyInfo(String identity, String platform) {
            this.identity = identity;
            this.platform = platform;
        }

        public KeyInfo(String identity, String platform, String app) {
            this.identity = identity;
            this.platform = platform;
            this.app = app;
        }

        private String identity;
        private String platform;
        private String app;

        public String buildKey() {
            return String.format("%s_%s_%s", identity, platform, app);
        }
    }

}
