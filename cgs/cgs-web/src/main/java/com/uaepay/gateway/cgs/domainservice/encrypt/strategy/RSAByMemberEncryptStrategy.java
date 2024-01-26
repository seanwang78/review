package com.uaepay.gateway.cgs.domainservice.encrypt.strategy;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.util.RsaUtil;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.domainservice.MemberService;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategy;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/7/25
 * @since 0.1
 */
@Slf4j
@Service
public class RSAByMemberEncryptStrategy implements EncryptStrategy {


    @Autowired
    private MemberService memberService;
    @Autowired
    private UesClientV2 uesClientV2;


    @Override
    public String convert(String fieldName, String original, ContextParameter parameter) {
        EncryptContextV2 contextV2 = new EncryptContextV2();
        contextV2.setTicket(original);
        if (!uesClientV2.getDataByTicket(contextV2)) {
            log.error("RSAByMember解密ues报错: {}", original);
            return original;
        }
        // 获取ues解密后的原文
        original = contextV2.getPlainData();

        ReceiveOrderContext context = parameter.getContext();
        // 接口未授权返回原文
        if(!context.getApiConfig().isAuthed()) {
            return original;
        }
        // h5的请求返回原文
        if (context.getPlatformType() == PlatformType.H5) {
            return original;
        }

        AccessMember accessMember = context.getAccessMember();
        Header header = context.getHeader();
        String memberId = accessMember.getMemberId(),
                partnerId = accessMember.getPartnerId(),
                hostApp = header.getHostApp(),
                deviceId = header.getDeviceId(),
                platform = header.getPlatformType();

        // 查询member的rsa公钥
        String publicKey = memberService.queryMemberRSAPublicKey(memberId, partnerId, hostApp, deviceId, platform);
        if (StringUtils.isBlank(publicKey)) {
            return original;
        }

        try {
            return RsaUtil.encrypt(original, Charset.defaultCharset(), publicKey, 2048);
        } catch (Exception e) {
            log.error("RSAByMember", e);
            throw new ErrorException("加密异常");
        }
    }



    @Override
    public String getServiceCode() {
        return "MRSA";
    }
}