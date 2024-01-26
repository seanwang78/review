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
import com.uaepay.gateway.cgs.integration.grc.GrcClient;
import com.uaepay.gateway.cgs.integration.ufs.UfsImageService;
import com.uaepay.gateway.common.app.template.domainservice.language.LanguageService;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.gateway.common.facade.enums.LangType;
import com.uaepay.grc.connect.api.vo.domain.OtherEventInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

import static com.uaepay.gateway.cgs.common.RequestUtil.getHeader;
import static com.uaepay.gateway.common.app.template.constants.LanguageConstant.DEFAULT_LANGUAGE_CONFIG;

/**
 * 非登陆态上传图片.
 * <p>
 *
 * @author yusai
 * @date 2021-06-17 12:02.
 */
@RestController
@Slf4j
public class UnUploadFileController {

    @Autowired
    private UfsImageService ufsImageService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private GrcClient grcClient;


    @RequestMapping("/uploadUn")
    public ClientServiceResponse<Map> upload(MultipartHttpServletRequest request) {


        ClientServiceResponse<Map> response = null;

        String langType = request.getHeader(GatewayConstants.LANG);

        try {
            // 参数校验
            parameterCheck(request);
            String eventId = request.getParameter("bizId");
            String ticket = request.getParameter("ticket");

            if (StringUtils.isBlank(eventId) && StringUtils.isBlank(ticket)) {
                throw new InvalidParameterException("eventId or  ticket is null");
            }

            OtherEventInfo eventInfo = grcClient.querySingleOtherEvent(eventId, ticket);

            if (eventInfo == null) {
                throw new InvalidParameterException("eventId");
            }
            MultipartFile file = request.getFile("file");

            log.info("file [{}],[{}]", file.getOriginalFilename(), file.getSize());

            String fileId = ufsImageService.uploadImage(file.getInputStream(), null, false);

            response = ClientServiceResponse.build(GatewayReturnCode.SUCCESS,
                    null, ImmutableMap.of("fileId", fileId));

        } catch (Throwable e) {
            if (e instanceof MaxUploadSizeExceededException || e instanceof FileUploadBase.FileSizeLimitExceededException) {
                log.error("上传文件异常~", e);
                response = ClientServiceResponse.build(CgsReturnCode.MAX_UPLOAD_SIZE_EXCEEDED);
            } else if (e instanceof InvalidParameterException) {
                response = ClientServiceResponse.build(GatewayReturnCode.INVALID_PARAMETER, e.getMessage());
            } else if (e instanceof FailException) {
                response = ClientServiceResponse.build(((FailException) e).getCode(), e.getMessage());
            } else {
                log.error("上传文件异常~", e);
                response = ClientServiceResponse.build(GatewayReturnCode.SYSTEM_ERROR);
            }

            String message = languageService.resolveOrDefault(getLangType(langType).getCode(), response.getHead().getMsg(),
                    "TOC", DEFAULT_LANGUAGE_CONFIG);

            response.getHead().setMsg(message);
            ClientResponseUtil.handleResponseTID(response);
        }
        return response;
    }

    private void parameterCheck(MultipartHttpServletRequest request) {
        ParameterValidate.assertNotNull(GatewayConstants.PLATFORM, getHeader(request, GatewayConstants.PLATFORM, HttpHeaderKey.X_PLATFORM));
        ParameterValidate.assertNotNull(GatewayConstants.LANG, request.getHeader(GatewayConstants.LANG));
        ParameterValidate.assertNotNull("file", request.getFile("file"));
        ParameterValidate.assertNotNull("scene", request.getParameter("scene"));
        if (!StringUtils.equalsIgnoreCase(request.getParameter("scene"), "RISK")) {
            ParameterValidate.invalidParameter("scene");
        }
    }

    private LangType getLangType(String lang) {
        LangType langType = LangType.getByCode(lang);
        if (langType == null) {
            langType = LangType.ENGLISH;
        }
        return langType;
    }
}
