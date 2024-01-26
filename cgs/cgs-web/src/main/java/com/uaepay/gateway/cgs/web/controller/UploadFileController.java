package com.uaepay.gateway.cgs.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.service.common.CgsReturnCode;
import com.uaepay.gateway.cgs.app.service.integration.CacheClient;
import com.uaepay.gateway.cgs.common.CacheKeyUtil;
import com.uaepay.gateway.cgs.common.ClientResponseUtil;
import com.uaepay.gateway.cgs.common.HeaderUtil;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.constants.HttpHeaderKey;
import com.uaepay.gateway.cgs.domain.AccessToken;
import com.uaepay.gateway.cgs.domainservice.AccessVerifyService;
import com.uaepay.gateway.cgs.integration.ufs.UfsImageService;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.app.template.domainservice.language.LanguageService;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.gateway.common.facade.enums.LangType;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.util.Map;

import static com.uaepay.gateway.cgs.common.RequestUtil.getHeader;
import static com.uaepay.gateway.common.app.template.constants.LanguageConstant.DEFAULT_LANGUAGE_CONFIG;

/**
 * 文件上传 .
 * <p>
 *
 * @author yusai
 * @date 2020-02-19 12:35.
 */
@RestController
@Slf4j
public class UploadFileController {

    @Autowired
    private UfsImageService ufsImageService;

    @Autowired
    private AccessVerifyService accessVerifyService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private CacheClient cacheClient;

    @Value("${upload.limit.count:5}")
    private Integer uploadLimitCount;

    @Value("${upload.limit.expire.time:1h}")
    private Duration limitExpireTime;



    @RequestMapping("/upload")
    public ClientServiceResponse<Map> upload(MultipartHttpServletRequest request) {

        return upload(request,false);
    }

    /**
     * 加密上传文件
     *
     * @param request
     * @return
     */
    @RequestMapping("/upload-encrypt")
    public ClientServiceResponse<Map> uploadByEncrypt(MultipartHttpServletRequest request) {
        return upload(request,true);
    }


    /**
     * 外部通用上传文件
     * @param request
     * @return
     */
    @RequestMapping("/v1/file/upload")
    public ClientServiceResponse<Map> fileUpload(MultipartHttpServletRequest request) {
        return fileUpload(request, false);
    }


    private ClientServiceResponse<Map> upload(MultipartHttpServletRequest request,Boolean isEncrypt) {

        ClientServiceResponse<Map> response = null;

        try {
            // 参数校验
            parameterCheck(request);
            log.info("请求头信息: {},isEncrypt:{}", JSON.toJSONString(HeaderUtil.getHeadersInfo(request, true)),isEncrypt);

            String accessToken = getHeader(request, GatewayConstants.ACCESS_TOKEN, HttpHeaderKey.X_ACCESSTOKEN);
            if (StringUtils.isBlank(accessToken)) {
                accessToken = getFromSession(request);
            }
            String token = getHeader(request, GatewayConstants.TOKEN, HttpHeaderKey.X_TOKEN);
            accessToken = accessVerifyService.analysisAccessToken(accessToken, token);


            AccessTokenResponse accessTokenRes = accessVerifyService.verifyToken(accessToken);

            MultipartFile file = request.getFile("file");

            log.info("file [{}],[{}]",file.getOriginalFilename(),file.getSize());

            String fileId = ufsImageService.uploadImage(file.getInputStream(),accessTokenRes.getMemberId(),isEncrypt);

            response =  ClientServiceResponse.build(GatewayReturnCode.SUCCESS,
                    null,ImmutableMap.of("fileId",fileId));

        } catch (Throwable e) {
            if(e instanceof MaxUploadSizeExceededException || e instanceof FileUploadBase.FileSizeLimitExceededException){
                log.error("上传文件异常~",e);
                response =  ClientServiceResponse.build(CgsReturnCode.MAX_UPLOAD_SIZE_EXCEEDED);
            }else if(e instanceof  InvalidParameterException){
                response = ClientServiceResponse.build(GatewayReturnCode.INVALID_PARAMETER,e.getMessage());
            }else if(e instanceof FailException ){
                response = ClientServiceResponse.build(((FailException) e).getCode(),e.getMessage());
            }else {
                log.error("上传文件异常~",e);
                response =  ClientServiceResponse.build(GatewayReturnCode.SYSTEM_ERROR);
            }

            String langType = request.getHeader(GatewayConstants.LANG);

            String message = languageService.resolveOrDefault(getLangType(langType).getCode(), response.getHead().getMsg(),
                    "TOC",DEFAULT_LANGUAGE_CONFIG);

            response.getHead().setMsg(message);
            ClientResponseUtil.handleResponseTID(response);
        }
        return response;
    }


    private ClientServiceResponse<Map> fileUpload(MultipartHttpServletRequest request,Boolean isEncrypt) {

        ClientServiceResponse<Map> response = null;

        try {
            // 参数校验
            parameterFileUploadCheck(request);
            log.info("请求头信息: {},isEncrypt:{}", JSON.toJSONString(HeaderUtil.getHeadersInfo(request, true)),isEncrypt);

            String accessToken = request.getHeader(HttpHeaderKey.X_TOKEN);
            String identity = request.getHeader(HttpHeaderKey.X_UCID);
            String accountId = request.getHeader(HttpHeaderKey.X_ACCOUNTID);
            String deviceId = request.getHeader(HttpHeaderKey.X_DEVICEID);
            String appId = request.getHeader(HttpHeaderKey.X_HOSTAPP);
            String fileType = request.getParameter("fileType");
            AccessToken asToken = new AccessToken(identity, accountId, deviceId, appId, accessToken);
            log.info("AccessToken上传文件头信息：{}", JSON.toJSONString(asToken));

            // 验证kyc accessToken
            accessVerifyService.validateKycAccessToken(asToken);

            // 一个小时内一个用户只能上传5次
            checkUploadFrequency(asToken, fileType);

            MultipartFile file = request.getFile("file");

            log.info("{}],[{}]",file.getOriginalFilename(),file.getSize());

            String[] suffixArr = file.getOriginalFilename().split("\\.");
            String fileSuffix = suffixArr[suffixArr.length - 1];

            String fileId = ufsImageService.uploadImageOutter(file.getInputStream(), fileSuffix, identity, accountId, fileType, isEncrypt);

            response =  ClientServiceResponse.build(GatewayReturnCode.SUCCESS,
                    null,ImmutableMap.of("fileId",fileId));

        } catch (Throwable e) {
            if(e instanceof MaxUploadSizeExceededException || e instanceof FileUploadBase.FileSizeLimitExceededException){
                log.error("上传文件异常~",e);
                response =  ClientServiceResponse.build(CgsReturnCode.MAX_UPLOAD_SIZE_EXCEEDED);
            }else if(e instanceof  InvalidParameterException){
                response = ClientServiceResponse.build(GatewayReturnCode.INVALID_PARAMETER,e.getMessage());
            }else if(e instanceof FailException ){
                response = ClientServiceResponse.build(((FailException) e).getCode(),e.getMessage());
            }else {
                log.error("上传文件异常~",e);
                response =  ClientServiceResponse.build(GatewayReturnCode.SYSTEM_ERROR);
            }

            String langType = request.getHeader(GatewayConstants.LANG);

            String message = languageService.resolveOrDefault(getLangType(langType).getCode(), response.getHead().getMsg(),
                    "TOC",DEFAULT_LANGUAGE_CONFIG);

            response.getHead().setMsg(message);
            ClientResponseUtil.handleResponseTID(response);
        }
        return response;
    }

    private String getFromSession(MultipartHttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String)session.getAttribute(GatewayConstants.ACCESS_TOKEN);
    }

    private void parameterCheck(MultipartHttpServletRequest request) {
        ParameterValidate.assertNotNull(GatewayConstants.PLATFORM, getHeader(request, GatewayConstants.PLATFORM, HttpHeaderKey.X_PLATFORM));
        ParameterValidate.assertNotNull(GatewayConstants.LANG,request.getHeader(GatewayConstants.LANG));
        ParameterValidate.assertNotNull("file",request.getFile("file"));
    }

    private void parameterFileUploadCheck(MultipartHttpServletRequest request) {
        ParameterValidate.assertNotNull(GatewayConstants.PLATFORM, getHeader(request, GatewayConstants.PLATFORM, HttpHeaderKey.X_PLATFORM));
        ParameterValidate.assertNotNull(GatewayConstants.LANG, request.getHeader(GatewayConstants.LANG));
        ParameterValidate.assertNotNull(HttpHeaderKey.X_TOKEN, request.getHeader(HttpHeaderKey.X_TOKEN));
        ParameterValidate.assertNotNull(HttpHeaderKey.X_UCID, request.getHeader(HttpHeaderKey.X_UCID));
        ParameterValidate.assertNotNull(HttpHeaderKey.X_ACCOUNTID, request.getHeader(HttpHeaderKey.X_ACCOUNTID));
        ParameterValidate.assertNotNull(HttpHeaderKey.X_DEVICEID, request.getHeader(HttpHeaderKey.X_DEVICEID));
        ParameterValidate.assertNotNull(HttpHeaderKey.X_HOSTAPP, request.getHeader(HttpHeaderKey.X_HOSTAPP));
        ParameterValidate.assertNotNull("fileType", request.getParameter("fileType"));
        ParameterValidate.assertNotNull("file",request.getFile("file"));
    }

    private LangType getLangType(String lang) {
        LangType langType = LangType.getByCode(lang);
        if (langType == null) {
            langType = LangType.ENGLISH;
        }
        return langType;
    }


    /**
     * 校验上传频率
     */
    private void checkUploadFrequency(AccessToken asToken, String fileType){
        String cacheKey = CacheKeyUtil.getKey("UPLOAD_FILE:" + asToken.getIdentity(), asToken.getAccountId(), asToken.getAppId(), fileType);
        Long count = cacheClient.getCount(cacheKey);
        if (count == 1){
            cacheClient.setExpireTime(cacheKey, limitExpireTime.getSeconds());
        }
        if (count > uploadLimitCount){
            log.info("在"+ limitExpireTime.getSeconds()/60 +"分钟内，" + "上传频率过高,当前上传次数："+ count +"，限定次数为：" + uploadLimitCount);
            throw new GatewayFailException(GatewayReturnCode.RATE_LIMIT_REJECT, "upload too frequent, try again later");
        }
    }
}
