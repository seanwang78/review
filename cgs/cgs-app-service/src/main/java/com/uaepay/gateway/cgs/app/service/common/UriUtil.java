package com.uaepay.gateway.cgs.app.service.common;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zc
 */
public class UriUtil {

    public static String getDomain(String uri) {
        if (StringUtils.isBlank(uri)) {
            return null;
        }
        try {
            String result = new URI(uri).getAuthority();
            return StringUtils.defaultIfBlank(result, uri);
        } catch (URISyntaxException e) {
            return uri;
        }
    }

}
