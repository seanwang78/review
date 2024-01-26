package com.uaepay.gateway.cgs.ext.service.impl;

import static com.uaepay.gateway.cgs.app.service.common.CgsReturnCode.*;
import static com.uaepay.gateway.cgs.common.RequestUtil.getHeader;
import static com.uaepay.gateway.cgs.common.VersionUtil.getVersionDigits;
import static com.uaepay.gateway.common.app.template.constants.LanguageConstant.DEFAULT_LANGUAGE_CONFIG;
import static com.uaepay.gateway.common.facade.enums.GatewayReturnCode.INVALID_PARAMETER;
import static com.uaepay.gateway.common.facade.enums.GatewayReturnCode.SYSTEM_ERROR;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.uaepay.gateway.cgs.domainservice.maintenance.MaintenanceService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.dubbo.rpc.RpcException;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Iterables;
import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.cms.facade.domain.AppVersionInfo;
import com.uaepay.cms.facade.response.AppVersionResponse;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayResponse;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.common.ClientResponseUtil;
import com.uaepay.gateway.cgs.common.CommonUtils;
import com.uaepay.gateway.cgs.common.HeaderUtil;
import com.uaepay.gateway.cgs.common.VersionUtil;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.constants.HttpHeaderKey;
import com.uaepay.gateway.cgs.domain.AppUpgradeQuery;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.domain.TmsDomain;
import com.uaepay.gateway.cgs.domain.api.ApiConfig;
import com.uaepay.gateway.cgs.domainservice.AccessVerifyService;
import com.uaepay.gateway.cgs.domainservice.AppVersionService;
import com.uaepay.gateway.cgs.domainservice.ConsultRiskService;
import com.uaepay.gateway.cgs.domainservice.VoucherService;
import com.uaepay.gateway.cgs.domainservice.check.CheckContext;
import com.uaepay.gateway.cgs.domainservice.check.CheckHandle;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptFilter;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptFilterFactory;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import com.uaepay.gateway.cgs.domainservice.impl.SendGatewayMsg;
import com.uaepay.gateway.cgs.ext.service.ReceiveOrderService;
import com.uaepay.gateway.cgs.ext.service.assist.CheckAssist;
import com.uaepay.gateway.cgs.integration.acs.AcsGatewayApiService;
import com.uaepay.gateway.cgs.integration.app.AppService;
import com.uaepay.gateway.cgs.integration.member.MemberClient;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.app.template.common.util.GatewayJsonUtil;
import com.uaepay.gateway.common.app.template.domainservice.language.LanguageService;
import com.uaepay.gateway.common.app.template.domainservice.ratelimit.RateLimitService;
import com.uaepay.gateway.common.app.template.enums.RateLimitResultType;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.gateway.common.facade.enums.LangType;
import com.uaepay.grc.common.enums.HostAppEnum;
import com.uaepay.member.service.response.IdentityInfo;
import com.uaepay.member.service.response.MemberIntegratedResponse;
import com.uaepay.voucher.facade.response.ReqVoucherResponse;

@Service
public class ReceiveOrderServiceImpl implements ReceiveOrderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NO_SERVICE_PATH = "/";
    private static final String JSON_FORMAT_ERROR = "${jsonFormatError}";

    private static final String UNIT_TYPE_MEMBERID = "MEMBERID";
    private static final String UNIT_TYPE_IP = "IP";
    private static final String TOC = "TOC";

    /** 跳过升级的路径 */
    @Value("${config.upgrade.filter.paths:}")
    private List<String> skipUpgradePaths;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private AcsGatewayApiService acsGatewayApiService;

    @Autowired
    private AccessVerifyService accessVerifyService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private AppService appService;

    @Autowired
    private EncryptFilterFactory encryptFilterFactory;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private ConsultRiskService consultRiskService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private SendGatewayMsg sendGatewayMsg;

    @Autowired
    private CheckHandle checkHandle;

    @Autowired
    private AppVersionService appVersionService;

    @Autowired
    private MemberClient memberClient;

    @Autowired
    private CheckAssist checkAssist;

    @Value("${debug.log:false}")
    boolean debug;

    @Value("${debug.error:false}")
    boolean printError;

    @Value("${gateway.cgs.rateLimit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${config.sdk.minSupportVersion:0.0.0}")
    private String minSupportVersion;

    @Value("${config.sdk.verify.filter.api:}")
    private List<String> verifyFilterApis;

    @Value("${config.sdk.hostApps:payby,totok-pay,botim-pay,cashnow,superapp,gomap-pay,we-credit}")
    public List<String> hostApps;
    @Value("${config.sdk.default.hostApp:totok-pay}")
    public String defaultHostApp;
    @Value("${config.sdk.checkHostAppMinVersion:1.0.9}")
    public String checkHostAppMinVersion;

    @Value("${config.logging.ignore.apis:/cmsii/v1/app/language}")
    public List<String> ignoreLoggingApis;

    @Resource
    private MaintenanceService maintenanceService;

    @Override
    public void processHttpRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        StopWatch watch = StopWatch.createStarted();
        ReceiveOrderContext context = new ReceiveOrderContext(httpRequest, httpResponse, debug, ignoreLoggingApis);
        String response = null;
        try {
            if (maintenanceService.isUnderMaintenance()) {
                response = maintenanceService.getResponse();
                return;
            }

            initializeLanguage(context);

            initializeService(context);
            checkApiConfig(context);
            initializeHeader(context);
            checkHeader(context);

            logRequestHeader(context);
            parseAndCheckBody(context);
            logRequest(context);
            checkTokenAndSign(context);

            checkAssist.checkAuth(context);

            checkAssist.checkGuard(context);

            checkAssist.whitelistCheck(context);

            checkMinSupportVersion(context);
            // checkAppUpgrade(context);
            rateLimit(context);

            checkField(context);
            recordRequestVoucher(context);
            decryptReplace(context);
            // 风控埋点
            riskBuriedPoint(context);

            // 服务路由
            ClientGatewayResponse serviceResponse = appService.doService(context);
            putSession(context, serviceResponse);
            response = serviceResponse.getResponse();

            // 响应参数加密
            response = decryptResponseReplace(context, response, context.getApiConfig().getResponseEncryptConfig());

        } catch (Throwable e) {
            response = handleException(context, e);

            if (context.isDebug()) {
                logger.info("cgs error response: {}", response);
            }
        } finally {

            if (context.getHeader() != null && StringUtils.isNotBlank(context.getHeader().getReqVoucherNo())) {
                recordResponseVoucher(context, response);
            }
            response = handleResponseTID(response);
            watch.stop();
            writeResponse(context, response, watch.getTime(TimeUnit.MILLISECONDS));
            if (!maintenanceService.isUnderMaintenance()) {
                // 网关返回信息写入到es
                sendMsg(context, watch, response);
            }
        }
    }

    /**
     * 检查App升级
     */
    private void checkAppUpgrade(ReceiveOrderContext context) {

        try {
            // 如果不包含在跳过检查中 返回
            String vectorCode = context.getHeader().getHostApp(), service = context.getService();

            /*
             * 检测升级的条件：
             * 1.不包含在跳过升级路径中
             * 2.不包含在跳过载体中
             * 3.升级支持的平台
             */
            if (!skipUpgradePaths.contains(service)
                && appVersionService.supportPlatformType(context.getPlatformType())) {
                Header header = context.getHeader();
                LangType lang = context.getLang();
                // 如果version-code没有传，则取sdk版本号，一般version-code是payby app传入，sdk版本号则是将sdk转换成version-no
                String currentVersionNo =
                    StringUtils.defaultIfBlank(header.getVersionCode(), getVersionDigits(header.getSdkVersion()));
                PlatformType platformType = context.getPlatformType();

                AppUpgradeQuery query = new AppUpgradeQuery();
                query.setVersionCode(currentVersionNo);
                query.setSdkVersion(header.getSdkVersion());
                query.setLangType(lang.getCode());
                query.setPlatformType(platformType);
                query.setVectorCode(vectorCode);

                AppVersionResponse combo = appVersionService.checkUpgrade(query);
                if (combo != null) {
                    // 检查是否强制升级
                    if (combo.getGreaterVersion() != null) {
                        Integer isForce = combo.getGreaterVersion().getIsForce();
                        if (ObjectUtils.equals(isForce, Integer.valueOf(1))) {
                            throw new GatewayFailException(APP_VERSION_UPGRADE);
                        }
                    }
                    // 检查当前请求Api是否支持
                    if (combo.getCurrentVersion() != null) {
                        AppVersionInfo currentVersion = combo.getCurrentVersion();
                        String notSupportApis = currentVersion.getNotSupportApis();
                        if (StringUtils.countMatches(notSupportApis, service) > 0) {
                            // 说明当前请求的Api在当前版本已经不支持了
                            throw new GatewayFailException(API_NOT_SUPPORT);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("检查版本升级报错", e);
        }

    }

    public void checkHeader(ReceiveOrderContext context) {
        // 如果是h5 并且 HostApp H5已经指定了 则不走底下的逻辑
        if (context.getPlatformType() == PlatformType.H5 || context.getPlatformType() == PlatformType.MINIAPP) {

            // 如果host为空 则默认Payby
            if (StringUtils.isBlank(context.getHeader().getHostApp())) {
                context.getHeader().setHostApp(HostAppEnum.PAYBY.getHostApp());
            }
            return;
        }

        Header header = context.getHeader();

        String checkSdkVersion = checkHostAppMinVersion;
        String currentVersion = "0.0.0";
        // 非法的sdkVersion则默认0.0.0，因为可能是老版本
        if (StringUtils.defaultIfEmpty(header.getSdkVersion(), "").split("\\.").length == 3) {
            currentVersion = header.getSdkVersion();
        }
        int compare = VersionUtil.compare(checkSdkVersion, currentVersion);
        // checkSdkVersion > 当前版本
        if (compare > 0) {
            // cgs 统一把小于当前版本的APP 传入的非法host app 当作totok-pay 处理，为了兼容老版本设备指纹。
            String hostApp = header.getHostApp();
            if (!Iterables.contains(hostApps, hostApp)) {
                header.setHostApp(defaultHostApp);
            }
        }
        // checkSdkVersion <= 当前版本
        else if (compare <= 0) {
            // 校验大于等于当前版本号的必须传host-app、host-app-version字段
            ParameterValidate.assertNotBlank("host-app", header.getHostApp());
            ParameterValidate.assertNotBlank("host-app-version", header.getHostAppversion());
        }
    }

    private void checkMinSupportVersion(ReceiveOrderContext context) {
        try {
            Header header = context.getHeader();
            if (context.getApiConfig().isAuthed()) {
                // 检查App版本
                String sdkVersion = header.getSdkVersion();
                int compare = VersionUtil.compare(sdkVersion, minSupportVersion);
                if (compare < 1 && !verifyFilterApis.contains(context.getService())) {
                    AccessMember accessMember = context.getAccessMember();
                    String partnerId = accessMember.getPartnerId(), identity = accessMember.getIdentity();
                    MemberIntegratedResponse response = memberClient.queryIntegrateMember(partnerId, identity);
                    if (response != null) {
                        List<IdentityInfo> identities = response.getBaseMemberInfo().getIdentitys();
                        for (IdentityInfo info : identities) {
                            // 未核身
                            if (identity.equals(info.getIdentity())
                                && ObjectUtils.equals(Integer.valueOf(2), info.getStatus())) {
                                throw new GatewayFailException(UPGRADE_APP_TO_VERIFY_KYC);
                            }
                        }
                    }
                }
            }
        } catch (GatewayFailException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[checkMinSupportVersion -> exception]", e);
        }
    }

    /**
     * @Title: sendMsg
     * @Description: 会写网关信息到es
     * @param context
     * @param watch
     * @param response
     * @return: void
     */
    @SuppressWarnings("unchecked")
    private void sendMsg(ReceiveOrderContext context, StopWatch watch, String response) {
        try {
            long totalTime = watch.getTime(TimeUnit.MILLISECONDS);
            Optional<AccessMember> option = Optional.ofNullable(context.getAccessMember());
            AccessMember accessMember = option.orElse(new AccessMember());
            Header header = context.getHeader();
            ClientServiceResponse<Object> jsonresponse = JSONArray.parseObject(response, ClientServiceResponse.class);
            TmsDomain tms = TmsDomain.tmsBuilder().tid(TraceContext.traceId()).systemName("CGS")
                .setRequestTime(new Date(watch.getStartTime()))
                .setResponseTime(new Date(watch.getStartTime() + totalTime)).setTotalTime(totalTime)
                .setLang(context.getLang() == null ? "-" : context.getLang().getCode())
                .setPlatformType(context.getPlatformType() == null ? "-" : context.getPlatformType().toString())
                .setAppCode(context.getApiConfig() == null ? "-" : context.getApiConfig().getAppCode())
                .setApiType(context.getApiConfig() == null ? "-" : context.getApiConfig().getApiType().name())
                .setApiCode(context.getApiConfig() == null ? context.getHttpRequest().getPathInfo()
                    : context.getApiConfig().getApiCode())
                .setIdentity(accessMember.getIdentity()).setMemberId(accessMember.getMemberId())
                .setPartnerId(accessMember.getPartnerId()).setReturnCode(jsonresponse.getHead().getCode())
                .setReturnMsg(jsonresponse.getHead().getMsg())
                .setClientIp(CommonUtils.getIpAddress(context.getHttpRequest())).builder();

            if (header != null) {
                tms.setHeader(com.uaepay.gateway.cgs.domain.Header.headerBuilder().deviceId(header.getDeviceId())
                    .sdkVersion(header.getSdkVersion()).hostApp(header.getHostApp())
                    .hostAppversion(header.getHostAppversion()).utcOffsetSeconds(header.getUtcOffsetSeconds())
                    .builder());
            }

            sendGatewayMsg.sendResMsg(tms);
        } catch (Exception e) {
            logger.error("[ReceiveOrderService -> sendMsg -> error]", e);
        }
    }

    private void riskBuriedPoint(ReceiveOrderContext context) {
        // 风控埋点
        consultRiskService.riskBuriedPoint(context);
    }

    /**
     * 记录响应凭证
     */
    private void recordResponseVoucher(ReceiveOrderContext context, String response) {
        try {
            ApiConfig apiConfig = context.getApiConfig();
            if (apiConfig != null) {
                YesNoEnum recordVoucher = apiConfig.getRecordVoucher();
                if (recordVoucher == YesNoEnum.YES) {
                    voucherService.recordResponseVoucher(context, response);
                }
            }
        } catch (Throwable e) {
            logger.error("[ReceiveOrderService -> recordResponseVoucher -> error]", e);
        }
    }

    /**
     * 记录请求凭证
     * 
     * @return
     */
    private void recordRequestVoucher(ReceiveOrderContext context) {
        try {
            ApiConfig apiConfig = context.getApiConfig();
            YesNoEnum recordVoucher = apiConfig.getRecordVoucher();
            if (recordVoucher == YesNoEnum.YES) {
                ReqVoucherResponse response = voucherService.recordReqVoucher(context);
                if (response != null) {
                    String voucherNo = response.getVoucherNo();
                    context.getHeader().reqVoucherNo(voucherNo);
                }
            }
        } catch (Throwable e) {
            logger.error("[ReceiveOrderService -> recordRequestVoucher -> error]", e);
        }
    }

    /**
     * 初始化语言
     */
    private void initializeLanguage(ReceiveOrderContext context) {
        String lang = context.getHttpRequest().getHeader(GatewayConstants.LANG);
        LangType langType = LangType.getByCode(lang);

        if (langType == null || langType == LangType.CHINESE) {
            langType = LangType.ENGLISH;
        }

        context.setLang(langType);
    }

    /**
     * 解析服务
     */
    private void initializeService(ReceiveOrderContext context) {
        String service = context.getHttpRequest().getPathInfo();
        if (StringUtils.isBlank(service) || StringUtils.equals(service, NO_SERVICE_PATH)) {
            throw new GatewayFailException(GatewayReturnCode.SERVICE_NOT_AVAILABLE);
        }
        context.setService(service);
    }

    /**
     * 解析header信息
     */
    private void initializeHeader(ReceiveOrderContext context) {
        HttpServletRequest request = context.getHttpRequest();
        Header header = Header.newHeader();
        header.setHeaderMaps(HeaderUtil.getHeadersInfo(request, false));

        String platform = getHeader(request, GatewayConstants.PLATFORM, HttpHeaderKey.X_PLATFORM);
        PlatformType platformType = PlatformType.getByCode(platform);
        ParameterValidate.assertParsedEnumNotNull(GatewayConstants.PLATFORM, platformType);
        context.setPlatformType(platformType);

        String utc = getHeader(request, GatewayConstants.UTCOFFSET_SECONDS, HttpHeaderKey.X_TZOFFSET);
        if (StringUtils.isNotBlank(utc)) {
            try {
                header.utcOffsetSeconds(Integer.parseInt(utc));
            } catch (NumberFormatException e) {
                ParameterValidate.invalidParameter(GatewayConstants.UTCOFFSET_SECONDS);
            }
        }

        if (platformType == PlatformType.iOS || platformType == PlatformType.Android) {
            header.setSdkVersion(getHeader(request, GatewayConstants.SDKVERSIONS, HttpHeaderKey.X_SDKVERSION));
            header.setHostApp(getHeader(request, GatewayConstants.HOST_APP, HttpHeaderKey.X_HOSTAPP));
            header.setHostAppversion(
                getHeader(request, GatewayConstants.HOST_APP_VERSION, HttpHeaderKey.X_HOSTAPPVERSION));
            header.setHostAppChannel(
                getHeader(request, GatewayConstants.HOST_APP_CHANNEL, HttpHeaderKey.X_HOSTAPPCHANNEL));
            header.deviceId(getHeader(request, GatewayConstants.DEVICE_FINGERPRINT, HttpHeaderKey.X_DEVICEID));

            context.setAccessToken(getHeader(request, GatewayConstants.ACCESS_TOKEN, HttpHeaderKey.X_ACCESSTOKEN));
            context.setSign(getHeader(request, GatewayConstants.SIGN, HttpHeaderKey.X_SIGN));
            context.setToken(getHeader(request, GatewayConstants.TOKEN, HttpHeaderKey.X_TOKEN));
        }

        if (platformType == PlatformType.MINIAPP || platformType == PlatformType.MINIAPP_BOITM_PAY ) {
            context.setAccessToken(getHeader(request, GatewayConstants.ACCESS_TOKEN, HttpHeaderKey.X_ACCESSTOKEN));
            header.setHostApp(getHeader(request, GatewayConstants.HOST_APP, HttpHeaderKey.X_HOSTAPP));
        }

        if (platformType == PlatformType.H5 || platformType == PlatformType.Web) {
            header.setReferer(request.getHeader(GatewayConstants.REFERER));
            header.setHostApp(getHeader(request, GatewayConstants.HOST_APP, HttpHeaderKey.X_HOSTAPP));
        }
        header.setSaltKey(getHeader(request, GatewayConstants.SALT_KEY, HttpHeaderKey.X_SALT));
        header.setPlatformType(platformType.name().toUpperCase());

        // 内部版本号
        String versionCode = getHeader(request, GatewayConstants.VERSION_CODE, HttpHeaderKey.X_VERSIONCODE);
        header.setVersionCode(versionCode);

        header.setClientIp(CommonUtils.getIpAddress(context.getHttpRequest()));
        header.setTmxSession(getHeader(request, GatewayConstants.TMX_SESSION, HttpHeaderKey.X_TMXSESSION));
        context.setHeader(header);
    }

    /**
     * 解析http body
     */
    private void parseAndCheckBody(ReceiveOrderContext context) throws IOException {
        String body = IOUtils.toString(context.getHttpRequest().getInputStream(), GatewayConstants.DEFAULT_CHARSET);
        if (context.isDebug() && StringUtils.isNotBlank(body)) {
            logger.info("request body:{}", body);
        }
        JSONObject jsonBody = null;
        if (StringUtils.isNotBlank(body)) {
            try {
                jsonBody = JSON.parseObject(body);
            } catch (JSONException e) {
                throw new InvalidParameterException(JSON_FORMAT_ERROR);
            }
        }
        context.setBody(body);
        context.setJsonBody(jsonBody);
    }

    /**
     * 检查API配置
     */
    private void checkApiConfig(ReceiveOrderContext context) {
        ApiConfig config = acsGatewayApiService.getApiConfig(context.getService());
        if (config == null) {
            throw new GatewayFailException(GatewayReturnCode.SERVICE_NOT_AVAILABLE);
        } else {
            context.setApiConfig(config);
        }
    }

    /**
     * 有效检查token和sign
     */
    private void checkTokenAndSign(ReceiveOrderContext context) {
        ApiType apiType = context.getApiConfig().getApiType();
        // 如果ApiType=授权 或者 ApiType=条件 并且 token不为空 则校验token
        if (apiType == ApiType.AUTHED) {
            accessVerifyService.verifyTokenAndSign(context);
        }

        // ApiType = Condition
        if (apiType == ApiType.CONDITION) {
            boolean isVerify = false;

            // Android 和 iOS验证请求头中是否有Token
            if (context.getPlatformType() == PlatformType.Android || context.getPlatformType() == PlatformType.iOS) {
                isVerify =
                    StringUtils.isNotBlank(context.getToken()) || StringUtils.isNotBlank(context.getAccessToken());
            }

            // H5 和 Web验证Session中是否有Token
            else if (context.getPlatformType() == PlatformType.H5 || context.getPlatformType() == PlatformType.Web
                || context.getPlatformType() == PlatformType.MINIAPP) {
                HttpSession session = context.getHttpRequest().getSession(false);
                isVerify = session != null
                    && StringUtils.isNotBlank((String)session.getAttribute(GatewayConstants.ACCESS_TOKEN));
            }

            // 是否校验Token
            if (isVerify) {
                accessVerifyService.verifyTokenAndSign(context);
            }
        }
    }

    /**
     * 敏感信息，密文转换
     * 
     * @param context
     */
    private void decryptReplace(ReceiveOrderContext context) {
        if (StringUtils.isBlank(context.getApiConfig().getEncryptConfig())) {
            return;
        }
        EncryptFilter filter = encryptFilterFactory.create(context.getApiConfig().getEncryptConfig());
        if (filter == null) {
            return;
        }
        // 授权接口
        String saltValue;
        if (context.getApiConfig().getApiType() == ApiType.AUTHED) {
            saltValue = accessTokenService.useSalt(context.getAccessToken());
        }
        // 非授权接口
        else {
            String saltKey = context.getHeader().getSaltKey();
            saltValue = accessTokenService.useSalt(saltKey);
        }
        filter.filterReplace(context.getJsonBody(), new ContextParameter(saltValue, context));
    }

    private String decryptResponseReplace(ReceiveOrderContext context, String response, String encryptConfig)
        throws IOException {

        if (StringUtils.isBlank(response)) {
            return response;
        }

        ClientServiceResponse<Object> jsonResponse = GatewayJsonUtil.parseObject(response, ClientServiceResponse.class);

        if (jsonResponse.getBody() == null) {
            jsonResponse.setBody(new JSONObject());
        }

        EncryptFilter filter = encryptFilterFactory.create(encryptConfig);
        if (filter == null) {
            return response;
        }

        JSONObject body = (JSONObject)JSON.toJSON(jsonResponse.getBody());

        filter.filterReplace(body, new ContextParameter(context));
        jsonResponse.setBody(body);

        return GatewayJsonUtil.toJsonString(jsonResponse);
    }

    /**
     * 记录日志
     */
    private void logRequest(ReceiveOrderContext context) {
        if (context.isDebug()) {
            logger.info("request: {}", context.toLogString());
        }
    }

    /**
     * 速率限制
     *
     * @param context
     */
    private void rateLimit(ReceiveOrderContext context) {
        if (rateLimitEnabled) {
            try {
                String unitType = null;
                String unitKey = null;

                // 非授权接口,用IP限流
                if (!context.getApiConfig().isAuthed()) {
                    unitType = UNIT_TYPE_IP;
                    unitKey = CommonUtils.getIpAddress(context.getHttpRequest());
                } else {
                    unitType = UNIT_TYPE_MEMBERID;
                    unitKey = context.getAccessMember().getMemberId();
                }
                RateLimitResultType limitResultType =
                    rateLimitService.check(context.getApiConfig().getConfigId().toString(), unitType, unitKey,
                        context.getApiConfig().getRateLimitGroup());

                if (RateLimitResultType.PASS != limitResultType) {
                    logger.warn("rateLimit api config id = [{}]", context.getApiConfig().getConfigId());
                    throw new GatewayFailException(GatewayReturnCode.RATE_LIMIT_REJECT);
                }

            } catch (Exception e) {
                logger.error("call rateLimitService.check exception:", e);
                throw new GatewayFailException(GatewayReturnCode.RATE_LIMIT_REJECT);
            }
        }
    }

    /**
     * 根据配置校验字段
     */
    private void checkField(ReceiveOrderContext context) {
        CheckContext checkContext =
            new CheckContext(context.getJsonBody(), context.getApiConfig().getCheckFieldConfig());
        if (context.getAccessMember() != null) {
            checkContext.setMemberId(context.getAccessMember().getMemberId());
        }
        checkHandle.check(checkContext);
    }

    /**
     * 设置session
     */
    private void putSession(ReceiveOrderContext context, ClientGatewayResponse response) {
        if (context.getApiConfig().isAllowSessionMap()) {
            if (MapUtils.isNotEmpty(response.getSessionMap())) {
                for (Map.Entry<String, String> entry : response.getSessionMap().entrySet()) {
                    context.getHttpRequest().getSession(true).setAttribute(entry.getKey(), entry.getValue());
                }
            }
        } else {
            if (MapUtils.isNotEmpty(response.getSessionMap())) {
                logger.error("session set not allowed");
            }
        }
    }

    /**
     * 处理异常
     * 
     * @return json响应报文
     */
    private String handleException(ReceiveOrderContext context, Throwable e) {
        ClientServiceResponse<Void> response;
        if (e instanceof InvalidParameterException) {
            if (printError) {
                logger.info("CGS 参数校验错误:", e);
            }
            response = ClientServiceResponse.build(INVALID_PARAMETER, e.getMessage());
        } else if (e instanceof BizCheckFailException) {
            response = ClientServiceResponse.build(GatewayReturnCode.BIZ_CHECK_FAIL, e.getMessage());
        } else if (e instanceof GatewayFailException) {
            response = ClientServiceResponse.build(((GatewayFailException)e).getCode(), e.getMessage());
        } else if (e instanceof FailException) {
            response = ClientServiceResponse.build(GatewayReturnCode.BIZ_CHECK_FAIL, e.getMessage());
        } else if (e instanceof ErrorException) {
            logger.error("内部异常: {}" + ((ErrorException)e).getCode(), e);
            response = ClientServiceResponse.build(SYSTEM_ERROR);
        } else if (e instanceof RpcException) {
            logger.error("Dubbo调用异常", e);
            response = ClientServiceResponse.build(RPC_EXCEPTION);
        } else {
            logger.error("内部异常", e);
            response = ClientServiceResponse.build(SYSTEM_ERROR);
        }
        LangType langType;
        if (context == null || context.getLang() == null) {
            langType = LangType.ENGLISH;
        } else {
            langType = context.getLang();
        }
        String message = resolveMessage(langType, response.getHead().getMsg(), context);
        response.getHead().setMsg(message);
        return JSON.toJSONString(response);
    }

    private String resolveMessage(LangType langType, String message, ReceiveOrderContext context) {
        if (context != null && StringUtils.isNotBlank(context.getLangScene())) {
            return languageService.resolveOrDefaultV2(langType.getCode(), message,
                Arrays.asList(context.getLangScene(), TOC), DEFAULT_LANGUAGE_CONFIG);
        } else {
            return languageService.resolveOrDefault(langType.getCode(), message, TOC, DEFAULT_LANGUAGE_CONFIG);
        }
    }

    private String handleResponseTID(String response) {
        try {
            ClientServiceResponse<?> clientResponse =
                GatewayJsonUtil.parseObject(response, ClientServiceResponse.class);
            ClientResponseUtil.handleResponseTID(clientResponse);
            return GatewayJsonUtil.toJsonString(clientResponse);
        } catch (Exception e) {
            logger.error("[ReceiveOrderService -> handleResponseTID -> response: {} ]", response, e);
        }
        return response;
    }

    /**
     * 写响应
     */
    private void writeResponse(ReceiveOrderContext context, String gatewayResponse, long consumeMs) {
        if (context.isDebug()) {
            logger.info("response: {} ms", consumeMs);
        }
        HttpServletResponse httpResponse = context.getHttpResponse();
        httpResponse.setContentType("application/json;charset=" + GatewayConstants.DEFAULT_CHARSET_NAME);
        try {
            httpResponse.getOutputStream().write(gatewayResponse.getBytes(GatewayConstants.DEFAULT_CHARSET_NAME));
            httpResponse.flushBuffer();
        } catch (IOException e) {
            logger.info("响应异常", e);
        }
    }

    /**
     * 记录日志
     */
    private void logRequestHeader(ReceiveOrderContext context) {
        if (context.isDebug()) {
            logger.info("request headers: {}", HeaderUtil.getHeadersInfo(context.getHttpRequest(), true));
        }
    }

}
