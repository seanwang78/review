package com.uaepay.gateway.cgs.ext.service.assist;

import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.app.service.common.CgsReturnCode;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.app.service.integration.CmsClient;
import com.uaepay.gateway.cgs.app.service.integration.PtsClient;
import com.uaepay.gateway.cgs.common.CommonUtils;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.constants.HttpHeaderKey;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.integration.acs.AcsKeyConfigService;
import com.uaepay.gateway.cgs.integration.personal.PersonalClient;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.app.template.domainservice.setting.PartnerSettingService;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.gateway.common.facade.enums.LangType;
import com.uaepay.personal.service.facade.request.gurad.CheckGuardTokenRequest;
import com.uaepay.pts.ext.facade.response.GuardTokenValueResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * 检查辅助类 .
 * <p>
 *
 * @author yusai
 * @date 2020-07-15 08:17.
 */
@Slf4j
@Service
public class CheckAssist {

    private String arWhitelistKey = "ARAB:WHITELIST:{0}:KEY";

    private static final String LANGUAGE_AR_WHITELIST = "LANGUAGE_AR_WHITELIST";

    private String checkGuardTokenKey = "PERSONAL:GURADTOKENCHECK:{0}:KEY";

    @Value("${ar.whitelist.ttl:5m}")
    private Duration wlTTl;

    @Value("${guard.token.check.ttl:10m}")
    private Duration gtTTl;


    /** 跳过 guard 校验 */
    @Value("${config.guard.filter.service:}")
    private List<String> skipGuardService;

    @Autowired
    private CmsClient cmsClient;

    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;

    @Autowired
    private PartnerSettingService partnerSettingService;

    @Autowired
    private PtsClient ptsClient;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private AcsKeyConfigService acsKeyConfigService;

    @Autowired
    private PersonalClient personalClient;

    /**
     * 白名单校验
     *
     * @param context
     */
    public void whitelistCheck(ReceiveOrderContext context) {

        // 1、阿语降级
        arDegrade(context);

    }


    public void checkGuard(ReceiveOrderContext context) {

        if (context.getHeader() != null && context.getApiConfig().isAuthed()
                && !context.getApiConfig().isSkipGTkCheck() &&
                !skipGuardService.contains(context.getApiConfig().getAppCode()) &&
                (PlatformType.iOS == context.getPlatformType() ||
                        PlatformType.Android == context.getPlatformType())) {


            String swAG = acsKeyConfigService.queryMerchantSetting(context.getAccessMember().getPartnerId(),
                    "GUARD_TOKEN_CHECK_SWITCH_AG");



            if(StringUtils.equalsIgnoreCase("N",swAG)) {
                return;
            }

            Map<String,String> headerMaps = context.getHeader().getHeaderMaps();

            String guardToken = headerMaps.get(HttpHeaderKey.X_GUARDTOKEN.toLowerCase());
            if(StringUtils.isNotBlank(guardToken)) {
                boolean isPass = true;
                try {
                    isPass = checkGuardToken(context,guardToken);
                }catch (Exception e) {
                    log.error(String.format(CommonUtils.WARN_LOG_FORMAT,"guard Token check error"),e);
                }

                if(!isPass) {
                    throw new GatewayFailException(CgsReturnCode.GUARD_INTERCEPT);
                }
            }

            String sw = acsKeyConfigService.queryMerchantSetting(context.getAccessMember().getPartnerId(),
                    "GUARD_TOKEN_CHECK_SWITCH");

            if(StringUtils.equalsIgnoreCase("N",sw)) {
                return;
            }

            String deviceId = Optional.ofNullable(headerMaps.get(HttpHeaderKey.X_DEVICEID.toLowerCase()))
                    .orElse(headerMaps.get(GatewayConstants.DEVICE_FINGERPRINT.toLowerCase()));

            if (StringUtils.isNotBlank(guardToken)) {

                String identity = context.getAccessMember() == null ? null : context.getAccessMember().getIdentity();

                String partnerId = context.getAccessMember() == null ? null : context.getAccessMember().getPartnerId();
                if (StringUtils.isBlank(identity)) {
                    throw new GatewayFailException(GatewayReturnCode.INVALID_TOKEN);
                }

                if (StringUtils.isBlank(partnerId)) {
                    throw new GatewayFailException(GatewayReturnCode.INVALID_PARAMETER);
                }



                GuardTokenValueResponse response = ptsClient.getGuardTokenValue(deviceId,identity,partnerId);

                Map<String, Boolean> value = response.getValue();

                String token = accessTokenService.analysisToken(guardToken);

                if (value != null) {
                    Boolean isPass = Optional.ofNullable(value.get(token)).orElse(false);
                    if (isPass) {
                        return;
                    }
                }
                throw new GatewayFailException(CgsReturnCode.GUARD_INTERCEPT);
            }

        }
    }

    public void checkAuth(ReceiveOrderContext context) {
        if(context.getPlatformType() == PlatformType.MINIAPP_BOITM_PAY) {
            if(!context.getApiConfig().isAllow(context.getPlatformType())) {
                log.error("API不支持访问，apiCode:{},platformType:{}",context.getApiConfig().getApiCode(),
                        context.getPlatformType().getCode());
                throw new GatewayFailException(CgsReturnCode.API_NOT_SUPPORT);
            }
        }
    }

    // 阿语降级
    private void arDegrade(ReceiveOrderContext context) {

        if(LangType.ARAB == context.getLang() && context.getApiConfig().isAuthed() ) {

            Boolean isWhitelist = Optional.ofNullable(
                    redisTemplate.opsForValue().get(MessageFormat.format(arWhitelistKey,context.getAccessMember().getMemberId()))
            ).orElse(false);

            if( !isWhitelist ) {
                isWhitelist = cmsClient.isWhitelist(context.getAccessMember().getMemberId(), LANGUAGE_AR_WHITELIST);
                redisTemplate.opsForValue().set(
                        MessageFormat.format(arWhitelistKey, context.getAccessMember().getMemberId()), isWhitelist,
                        wlTTl.toMillis(), TimeUnit.MILLISECONDS);
                if( !isWhitelist ) {
                    context.setLang(LangType.ENGLISH);
                    return;
                }
            }
        }
    }

    private Boolean checkGuardToken(ReceiveOrderContext context,String guardToken) {
        String key = MessageFormat.format(checkGuardTokenKey,context.getAccessMember().getMemberId());
        boolean value = Optional.ofNullable(redisTemplate.opsForValue().get(key)).orElse(false);
        if(!value) {
            boolean isPass = personalClient.checkGuardToken(buildRequest(context, guardToken));
            if(isPass) {
                redisTemplate.opsForValue().set(key,true,gtTTl.getSeconds(),TimeUnit.SECONDS);
            }
            return isPass;
        }
        return true;
    }


    private CheckGuardTokenRequest buildRequest(ReceiveOrderContext context,String guardToken) {
        CheckGuardTokenRequest request = new CheckGuardTokenRequest();
        request.setGuardToken(guardToken);
        request.setHostApp(context.getHeader().getHostApp());
        request.setIdentity(context.getAccessMember().getIdentity());
        request.setMemberId(context.getAccessMember().getMemberId());
        request.setPartnerId(context.getAccessMember().getPartnerId());
        request.setLangType(context.getLang().getCode());

        return request;
    }
}
