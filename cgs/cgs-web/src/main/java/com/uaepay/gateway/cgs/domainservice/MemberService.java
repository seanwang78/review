package com.uaepay.gateway.cgs.domainservice;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/7/25
 * @since 0.1
 */
public interface MemberService {

    /**
     * 查询会员RSA公钥
     *
     * @param memberId 会员ID
     * @param partnerId 商户ID
     * @param hostApp  hostApp
     * @param deviceId 设备ID
     * @param platform 平台
     */
    String queryMemberRSAPublicKey(String memberId, String partnerId, String hostApp, String deviceId, String platform);

}
