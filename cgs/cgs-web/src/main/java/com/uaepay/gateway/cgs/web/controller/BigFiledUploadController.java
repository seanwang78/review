package com.uaepay.gateway.cgs.web.controller;

import com.google.common.collect.ImmutableMap;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.service.common.CgsReturnCode;
import com.uaepay.gateway.cgs.common.ClientResponseUtil;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.constants.HttpHeaderKey;
import com.uaepay.gateway.cgs.domainservice.AccessVerifyService;
import com.uaepay.gateway.cgs.integration.ufs.UfsImageService;
import com.uaepay.gateway.cgs.web.model.LiveFaceFileUpload;
import com.uaepay.gateway.common.app.template.common.util.GatewayJsonUtil;
import com.uaepay.gateway.common.app.template.domainservice.language.LanguageService;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.gateway.common.facade.enums.LangType;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.uaepay.gateway.cgs.common.RequestUtil.getHeader;
import static com.uaepay.gateway.common.app.template.constants.LanguageConstant.DEFAULT_LANGUAGE_CONFIG;


@RestController
@Slf4j
public class BigFiledUploadController {

    @Autowired
    private UfsImageService ufsImageService;

    @Autowired
    private AccessVerifyService accessVerifyService;

    @Autowired
    private LanguageService languageService;


    @RequestMapping(value = "bigfield/upload",method = {RequestMethod.POST})
    public ClientServiceResponse<Map> bigFiledUpload(HttpServletRequest request) {

        ClientServiceResponse<Map> response = null;

        try {
            // 参数校验
            parameterCheck(request);

            String accessToken = getHeader(request, GatewayConstants.ACCESS_TOKEN, HttpHeaderKey.X_ACCESSTOKEN);
            String token = getHeader(request, GatewayConstants.TOKEN, HttpHeaderKey.X_TOKEN);
            accessToken = accessVerifyService.analysisAccessToken(accessToken, token);
            AccessTokenResponse accessTokenRes = accessVerifyService.verifyToken(accessToken);

            String body = IOUtils.toString(request.getInputStream(), GatewayConstants.DEFAULT_CHARSET);

            ParameterValidate.assertNotNull("body", body);

            LiveFaceFileUpload liveFaceFileUpload = GatewayJsonUtil.parseObject(body, LiveFaceFileUpload.class);
            ParameterValidate.assertNotNull("liveFaceFile", liveFaceFileUpload.getLiveFaceFile());

            String fileBase64Code = liveFaceFileUpload.getLiveFaceFile();

            byte[] buf = fileBase64Code.getBytes(StandardCharsets.UTF_8);
            InputStream is = new ByteArrayInputStream(buf);

            String fileTag  = ufsImageService.uploadImage(is,accessTokenRes.getMemberId(),false);

            response =  ClientServiceResponse.build(GatewayReturnCode.SUCCESS,
                    null,ImmutableMap.of("fileTag",fileTag));

        } catch (Throwable e) {
            if(e instanceof MaxUploadSizeExceededException || e instanceof FileUploadBase.FileSizeLimitExceededException){
                log.error("字段上传异常~",e);
                response =  ClientServiceResponse.build(CgsReturnCode.MAX_UPLOAD_SIZE_EXCEEDED);
            }else if(e instanceof  InvalidParameterException){
                response = ClientServiceResponse.build(GatewayReturnCode.INVALID_PARAMETER,e.getMessage());
            }else if(e instanceof FailException ){
                response = ClientServiceResponse.build(((FailException) e).getCode(),e.getMessage());
            }else {
                log.error("字段上传~",e);
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

    private void parameterCheck(HttpServletRequest request) {
        ParameterValidate.assertNotNull(GatewayConstants.PLATFORM, getHeader(request, GatewayConstants.PLATFORM, HttpHeaderKey.X_PLATFORM));
        ParameterValidate.assertNotNull(GatewayConstants.LANG,request.getHeader(GatewayConstants.LANG));
    }

    private LangType getLangType(String lang) {
        LangType langType = LangType.getByCode(lang);
        if (langType == null) {
           langType = LangType.ENGLISH;
        }
        return langType;
    }
}
