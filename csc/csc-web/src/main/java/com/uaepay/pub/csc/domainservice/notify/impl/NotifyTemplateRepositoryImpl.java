package com.uaepay.pub.csc.domainservice.notify.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.validate.exception.ValidationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.uaepay.caffeine.starter.constants.CaffeineCacheConstants;
import com.uaepay.pub.csc.domainservice.notify.NotifyTemplateRepository;
import com.uaepay.common.util.VelocityUtil;

/**
 * @author zc
 */
@Service
public class NotifyTemplateRepositoryImpl implements NotifyTemplateRepository {

    public static final String CACHE_NAME_NOTIFY_TEMPLATE = "NOTIFY_TEMPLATE";
    public static final String TEAMS_HEAD_TEMPLATE_FILEPATH = "/META-INF/template/teams_notify_template.vm";

    @Override
    @Cacheable(value = CACHE_NAME_NOTIFY_TEMPLATE, cacheManager = CaffeineCacheConstants.CACHE_MANAGER)
    public String loadTemplate(String filePath) {
        try {
            File file = new ClassPathResource(filePath).getFile();
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String buildTeamsNotifyInfo(NotifyTemplateTypeEnum templateType, Map<String, Object> params) {
        try{
            String title = VelocityUtil.getString(loadTemplate(templateType.getEmailTitle()), params);
            String body = VelocityUtil.getString(loadTemplate(templateType.getEmailBody()), params);

//            JSONObject notifyJson = JSONObject.parseObject(teamsNotifyTemplate, JSONObject.class, Feature.DisableSpecialKeyDetect);
//            notifyJson.put("title", title);
//            notifyJson.put("text", body);

            Map<String, Object> teamsNotifyParams = new HashMap<>();
            teamsNotifyParams.put("title", title);
            teamsNotifyParams.put("text", body);
            return VelocityUtil.getString(loadTemplate(TEAMS_HEAD_TEMPLATE_FILEPATH) ,teamsNotifyParams);
        }
        catch (ValidationException e){
            throw new RuntimeException(e);
        }
    }


}
