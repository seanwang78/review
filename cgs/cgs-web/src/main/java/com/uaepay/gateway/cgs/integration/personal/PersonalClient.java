package com.uaepay.gateway.cgs.integration.personal;

import com.alibaba.fastjson.JSON;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.gateway.cgs.common.CommonUtils;
import com.uaepay.personal.service.facade.gurad.GuardFacade;
import com.uaepay.personal.service.facade.request.gurad.CheckGuardTokenRequest;
import com.uaepay.personal.service.facade.response.guard.CheckGuardTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;


/**
 *  personal 客户端.
 * <p>
 *
 * @author yusai
 * @date 2021-05-19 18:03.
 */
@Slf4j
@Component
public class PersonalClient {

    @Reference
    GuardFacade guardFacade;

    public boolean checkGuardToken(CheckGuardTokenRequest request){
        try {

            log.info("PersonalClient.checkGuardToken request ~ {}", JSON.toJSONString(request));

            CheckGuardTokenResponse response = guardFacade.checkGuardToken(request);

            log.info("PersonalClient.checkGuardToken response~ {} ", JSON.toJSONString(response));

            if(response != null && response.getApplyStatus() == ApplyStatusEnum.SUCCESS) {
                return true;
            }

        }catch (Exception e) {
            log.error(String.format(CommonUtils.WARN_LOG_FORMAT,"PersonalClient.checkGuardToken Exception"),e);
            return true;
        }
        return false;
    }
}
