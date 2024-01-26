package com.uaepay.pub.csc.core.common.util;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * http工具类
 * @author xiayibing
 */
public class HttpUtil {
    protected static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * http post请求
     * @param info
     * @return
     */
    public static void httpPost(String info, String url) throws IOException {
//        logger.info("TeamsNotifyInfo:"+info);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, info);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
    }

}
