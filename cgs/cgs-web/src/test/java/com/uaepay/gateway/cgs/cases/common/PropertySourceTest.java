package com.uaepay.gateway.cgs.cases.common;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;

public class PropertySourceTest {

    @Test
    public void test() {
        MutablePropertySources propertySources = new MutablePropertySources();

        Map<String, Object> appMap = new HashMap<String, Object>();
        appMap.put("body", "皮囊");
        MapPropertySource appSource = new MapPropertySource("cgs", appMap);
        propertySources.addLast(appSource);

        Map<String, Object> defaultMap = new HashMap<String, Object>();
        defaultMap.put("invalidParameter", "参数错误");
        MapPropertySource defaultSource = new MapPropertySource("default", defaultMap);
        propertySources.addLast(defaultSource);

//        StandardEnvironment environment = new StandardEnvironment();
//        environment.validateRequiredProperties();

        PropertySourcesPropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);


        String message = propertyResolver.resolvePlaceholders("${invalidParameter}: ${body}");
        System.out.println(message);

    }

}
