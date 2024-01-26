package com.uaepay.gateway.cgs.integration.member.impl;

import com.uaepay.basis.beacon.common.util.JsonUtil;
import com.uaepay.gateway.cgs.app.service.common.CgsReturnCode;
import com.uaepay.gateway.cgs.integration.member.MaPayPasswordService;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.member.service.facade.IPayPwdFacade;
import com.uaepay.member.service.request.PayPwdDecryptRequest;
import com.uaepay.member.service.response.PayPwdDecryptResponse;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MaPayPasswordServiceImpl implements MaPayPasswordService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    protected String clientId;

    @Reference
    IPayPwdFacade iPayPwdFacade;

    @Override
    public String decryptPayPassword(String paypwd, String salt) {
        PayPwdDecryptRequest request = new PayPwdDecryptRequest();
        request.setPayPwd(paypwd);
        request.setOrgCode("CFCA");
        request.setSalt(salt);
        logger.info("ma支付密码解密请求: {}", request);
        PayPwdDecryptResponse response = iPayPwdFacade.decryptPayPwd(request);
        logger.info("ma支付密码响应: response = {}", JsonUtil.toJsonStringMute(response));
        if (!response.isSuccess()) {
            throw new GatewayFailException(CgsReturnCode.DECRYPT_PAYPWD_ERROR);
        }
        return response.getPassword();
    }

}
