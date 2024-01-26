package com.uaepay.pub.csc.ext.integration.impl;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.ufs2.client.common.domain.request.share.GetShareFileUrlRequest;
import com.uaepay.basis.ufs2.client.common.domain.request.share.PutShareFileFromFileRequest;
import com.uaepay.basis.ufs2.client.common.domain.response.GetFileUrlResponse;
import com.uaepay.basis.ufs2.client.common.domain.response.share.PutShareFileResponse;
import com.uaepay.basis.ufs2.client.proxy.UfsImageClient;
import com.uaepay.pub.csc.core.common.util.FileUtil;
import com.uaepay.pub.csc.ext.integration.UfsShareClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Duration;

/**
 * @author lzb
 */
@Slf4j
@Component
public class UfsShareClientImpl implements UfsShareClient {

    @Autowired
    UfsImageClient ufsClient;

    @Override
    public String upload(File file, Duration expireDuration, boolean deleteAfterUploaded) {
        PutShareFileFromFileRequest request =
            PutShareFileFromFileRequest.builder().sourceFile(file).expireDuration(expireDuration).build();
        PutShareFileResponse response = ufsClient.putFile(request);
        if (response.getApplyStatus() != ApplyStatusEnum.SUCCESS) {
            log.error("Upload from file failed: {}", response);
            throw new ErrorException("Upload from file failed");
        }
        if (deleteAfterUploaded) {
            FileUtil.deleteFile(file);
        }
        return response.getFileTag();
    }

    @Override
    public String getUrl(String fileTag) {
        GetShareFileUrlRequest request = GetShareFileUrlRequest.builder().fileTag(fileTag).build();
        GetFileUrlResponse response = ufsClient.getFileUrl(request);
        if (response.getApplyStatus() == ApplyStatusEnum.SUCCESS) {
            return response.getUrl();
        }
        log.error("获取文件url异常: {}, {}", fileTag, response);
        throw new ErrorException("获取文件Url异常");
    }
}
