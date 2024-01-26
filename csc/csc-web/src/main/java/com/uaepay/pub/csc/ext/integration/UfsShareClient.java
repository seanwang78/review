package com.uaepay.pub.csc.ext.integration;

import java.io.File;
import java.time.Duration;

/**
 * @author lzb
 */
public interface UfsShareClient {

    /**
     * 上传文件
     *
     * @param file
     *            File
     * @param expireDuration
     *            过期时间
     * @param deleteAfterUploaded
     *            上传后删除文件
     * @return File tag
     */
    String upload(File file, Duration expireDuration, boolean deleteAfterUploaded);

    /**
     * 获取内网文件URL
     * 
     * @param fileTag
     *            文件ID
     */
    String getUrl(String fileTag);

}
