package com.uaepay.gateway.cgs.domainservice.impl;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.common.util.ShaUtil;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.app.service.integration.KycServiceClient;
import com.uaepay.gateway.cgs.app.service.integration.PtsClient;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.constants.HttpHeaderKey;
import com.uaepay.gateway.cgs.domain.AccessToken;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.domainservice.AccessVerifyService;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.uaepay.gateway.cgs.app.template.constants.MessageConstants.DEVICE_NO_MATCH;
import static com.uaepay.gateway.cgs.app.template.constants.MessageConstants.HOST_APP_NO_MATCH;

@Service
public class AccessVerifyServiceImpl implements AccessVerifyService {

    private static final String PTS_SUCCESS = "0";
    private static final String MEMBER_NOT_SAME = "69408";


    @Value("${token.issuer:test}")
    private String issuer;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private PtsClient ptsClient;

    @Autowired
    private KycServiceClient kycServiceClient;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void verifyTokenAndSign(ReceiveOrderContext context) {
        AccessTokenResponse accessToken;
        switch (context.getPlatformType()) {
            case iOS:
            case Android:
                accessToken = checkAppAccess(context);
                break;
            case H5:
            case Web:
                accessToken = checkWebAccess(context);
                break;
            case MINIAPP:
                accessToken = checkMiniAppAccess(context);
                break;
            case MINIAPP_BOITM_PAY:
                accessToken = checkMiniAppBotimPayAccess(context);
                break;
            default:
                throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
        }
        context.setAccessMember(buildAccessMember(accessToken));
    }

    @Override
    public AccessTokenResponse verifyToken(String accessToken) throws FailException {
        return ptsClient.getAccessToken(accessToken);
    }

    @Override
    public String analysisAccessToken(String accessToken, String token) {
        if (StringUtils.isBlank(accessToken) && StringUtils.isBlank(token)) {
            ParameterValidate.invalidParameter(GatewayConstants.TOKEN);
        }
        if (StringUtils.isNotBlank(token)) {
            // 新token解析
            return accessTokenService.analysisToken(token);
        }
        return accessToken;
    }

    @Override
    public void validateKycAccessToken(AccessToken asToken) {
        boolean validatePass = kycServiceClient.validateAccessToken(asToken.getAccessToken(),
                asToken.getIdentity(), asToken.getAccountId(), asToken.getDeviceId(), asToken.getAppId());
        if (!validatePass) {
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED, "accessToken unauthorized or expired");
        }
    }

    /**
     * 校验app授权
     *
     * @return 访问令牌信息，非空
     * @throws GatewayFailException
     *             验证失败是抛出
     */
    private AccessTokenResponse checkAppAccess(ReceiveOrderContext context) throws GatewayFailException {
        checkNewToken(context);
        Header header = context.getHeader();

        ParameterValidate.assertNotBlank(GatewayConstants.SIGN, context.getSign());
        ParameterValidate.assertNotBlank(GatewayConstants.ACCESS_TOKEN, context.getAccessToken());

        AccessTokenResponse accessToken = verifyToken(context.getAccessToken());

        if (StringUtils.isNotBlank(accessToken.getDeviceId())
                && !accessToken.getDeviceId().equals(context.getHeader().getDeviceId())) {
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED, DEVICE_NO_MATCH);
        }
        if (StringUtils.isNotBlank(header.getHostApp()) && StringUtils.isNotBlank(accessToken.getHostApp())) {
            if (!StringUtils.equals(header.getHostApp(), accessToken.getHostApp())) {
                throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED, HOST_APP_NO_MATCH);
            }
        }

        String plain = context.getBody() + accessToken.getAccessKey();
        String sign = ShaUtil.getSha256(plain, GatewayConstants.DEFAULT_CHARSET);

        // 2020-04-16 修改成不区分大小写
        if (!StringUtils.equalsIgnoreCase(sign, context.getSign())) {
            logger.info("sign={}, plain={}", sign, plain);
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
        }

        return accessToken;
    }

    private void checkNewToken(ReceiveOrderContext context) {
        String accessToken = context.getAccessToken();
        String token = context.getToken();
        context.setAccessToken(analysisAccessToken(accessToken, token));
    }

    /**
     * 校验web授权
     *
     * @return 访问令牌信息，非空
     * @throws GatewayFailException
     *             验证失败是抛出
     */
    private AccessTokenResponse checkWebAccess(ReceiveOrderContext context) throws GatewayFailException {
        HttpSession session = context.getHttpRequest().getSession(false);
        // 无session，未授权
        if (session == null) {
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
        }
        // 有session，未绑定token
        String token = (String)session.getAttribute(GatewayConstants.ACCESS_TOKEN);
        if (StringUtils.isBlank(token)) {
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
        }
        if (context.isDebug()) {
            logger.info("web access token: {}", StringUtils.abbreviate(token, StringUtils.length(token) / 2));
        }
        AccessTokenResponse accessToken = verifyToken(token);
        context.setAccessToken(token);
        return accessToken;
    }

    /**
     * 校验MINIAPP授权
     *
     * @return 访问令牌信息，非空
     * @throws GatewayFailException
     *             验证失败是抛出
     */
    private AccessTokenResponse checkMiniAppAccess(ReceiveOrderContext context) throws GatewayFailException {
        HttpSession session = context.getHttpRequest().getSession(false);
        // 无session，未授权
        if (session == null) {
            logger.info("session 为空~");
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
        }
        // 有session，未绑定token
        String token = (String)session.getAttribute(GatewayConstants.ACCESS_TOKEN);
        if (StringUtils.isBlank(token)) {
            logger.info("session 中token为空~");
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
        }

        if(!StringUtils.equalsIgnoreCase(context.getAccessToken(),token)) {

            if(StringUtils.equalsIgnoreCase(issuer,"sim") &&
                    StringUtils.equalsIgnoreCase(context.getAccessToken(),"localhost")) {
                // 测试环境白名单
            }else {
                logger.info("传入的accessToken:{} 与 session token:{} 不一致", context.getAccessToken(), token);
                throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
            }
        }

        if (context.isDebug()) {
            logger.info("web access token: {}", StringUtils.abbreviate(token, StringUtils.length(token) / 2));
        }
        AccessTokenResponse accessToken = verifyToken(token);
        context.setAccessToken(token);
        return accessToken;
    }

    /**
     * 校验botim-pay小程序
     *
     * @return 访问令牌信息，非空
     * @throws GatewayFailException
     *             验证失败是抛出
     */
    private AccessTokenResponse checkMiniAppBotimPayAccess(ReceiveOrderContext context) throws GatewayFailException {
        String token = context.getAccessToken();
        if (StringUtils.isBlank(token)) {
            logger.info("Header 中token 为空");
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
        }
        AccessTokenResponse accessToken = verifyToken(token);
        String ucid = context.getHeader().getHeaderMaps().get(HttpHeaderKey.X_UCID.toLowerCase());
        if(!StringUtils.equalsIgnoreCase(ucid,accessToken.getIdentity())) {
            logger.info("传入的x-ucid:{} 与 登录用户不一致:{} 不一致", ucid, accessToken.getIdentity());
            throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED);
        }
        context.setAccessToken(token);
        return accessToken;
    }

    private AccessMember buildAccessMember(AccessTokenResponse accessToken) {
        AccessMember result = new AccessMember();
        result.setMemberId(accessToken.getMemberId());
        result.setIdentity(accessToken.getIdentity());
        result.setPartnerId(accessToken.getPartnerId());
        result.setHasPayPwd(accessToken.getHasPayPwd() == null ? YesNoEnum.NO : accessToken.getHasPayPwd());
        result.setHasRealName(accessToken.getHasRealName() == null ? YesNoEnum.NO : accessToken.getHasRealName());
        return result;
    }

}
