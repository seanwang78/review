package com.uaepay.gateway.cgs.integration.ufs;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.ufs2.client.common.domain.request.share.GetShareFileMetaRequest;
import com.uaepay.basis.ufs2.client.common.domain.request.share.PutShareFileFromStreamRequest;
import com.uaepay.basis.ufs2.client.common.domain.response.GetFileResponse;
import com.uaepay.basis.ufs2.client.common.domain.response.share.PutShareFileResponse;
import com.uaepay.basis.ufs2.client.proxy.UfsImageClient;
import com.uaepay.gateway.cgs.app.service.common.CgsReturnCode;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.uaepay.gateway.cgs.app.template.constants.MessageConstants.FILE_NO_PERMISSION;

/**
 * ufs image 服务类 .
 * <p>
 *
 * @author yusai
 * @date 2020-02-21 14:30.
 */
@Service
@Slf4j
public class UfsImageService {

    @Value("${spring.application.name}")
    private String clientId;

    @Qualifier("ufsImageClientDefault")
    @Autowired
    private UfsImageClient ufsImageClientDefault;

    // 提供外部指定的clientId的ufs客户端（与默认的不同）
    @Qualifier("ufsImageClientOutter")
    @Autowired
    private UfsImageClient ufsImageClientOutter;

    @Value("${ufs.image.timeout}")
    private Duration timeout;

    /**
     * 上传图片
     *
     * @param in
     * @param memberId
     * @return
     */
    public String uploadImage(InputStream in,String memberId,Boolean isEncrypt){

        PutShareFileFromStreamRequest request = PutShareFileFromStreamRequest.builder()
                .inputStream(in).expireDuration(timeout).encrypt(isEncrypt).build();

        if(StringUtils.isNotBlank(memberId)) {
            request.setMetaData(ImmutableMap.of("memberId", memberId));
        }

        try {

            log.info("cgs --> ufs uploadImage.request，memberId:[{}],length:[{}]",memberId);

            PutShareFileResponse response = ufsImageClientDefault.putFile(request);

            log.info("ufs --> cgs uploadImage.response:[{}]", JSON.toJSONString(response));

            if(response == null || ApplyStatusEnum.SUCCESS != response.getApplyStatus()) {
                throw new GatewayFailException(CgsReturnCode.UPLOAD_FILE_ERROR);
            }

            return response.getFileTag();

        } catch (Throwable e) {
            log.error("ufs call fail upload :",e);
            throw new GatewayFailException(CgsReturnCode.UFS_CALL_FAIL);
        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("ufsImage upload close inputStream exception :",e);
                }
            }
        }

    }


    /**
     * 外部上传图片
     *
     * @param in
     * @param identity
     * @return
     */
    public String uploadImageOutter(InputStream in, String fileSuffix, String identity, String accountId, String fileType, Boolean isEncrypt){

        PutShareFileFromStreamRequest request = PutShareFileFromStreamRequest.builder()
                .inputStream(in).fileSuffix(fileSuffix).encrypt(isEncrypt).build();

        Map<String, String> map = Maps.newHashMap();
        if(StringUtils.isNotBlank(identity)) {
            map.put("identity", identity);
        }
        if(StringUtils.isNotBlank(accountId)) {
            map.put("accountId", accountId);
        }
        if(StringUtils.isNotBlank(fileType)) {
            map.put("fileType", fileType);
        }
        if(StringUtils.isNotBlank(fileSuffix)) {
            map.put("fileSuffix", fileSuffix);
        }
        if (MapUtils.isNotEmpty(map)) {
            request.setMetaData(map);
        }

        try {

            log.info("cgs --> ufs outUploadImage.request，identity:[{}],length:[{}]",identity);

            PutShareFileResponse response = ufsImageClientOutter.putFile(request);

            log.info("ufs --> cgs outUploadImage.response:[{}]", JSON.toJSONString(response));

            if(response == null || ApplyStatusEnum.SUCCESS != response.getApplyStatus()) {
                throw new GatewayFailException(CgsReturnCode.UPLOAD_FILE_ERROR);
            }

            return response.getFileTag();

        } catch (Throwable e) {
            log.error("ufs call fail upload :",e);
            throw new GatewayFailException(CgsReturnCode.UFS_CALL_FAIL);
        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("ufsImage upload close inputStream exception :",e);
                }
            }
        }

    }


    /**
     * 校验 fileID 是否属于当前登录用户
     *
     * @param fileIds
     * @param memberId
     */
    public void checkFileId(List<String> fileIds,String memberId) {

        try {
            log.info("cgs --> ufs getFileMeta.request:[{}],memberId:[{}]",JSON.toJSONString(fileIds),memberId);
            fileIds.stream().forEach(fileId ->{
                GetShareFileMetaRequest request = GetShareFileMetaRequest.builder().fileTag(fileId).build();
                GetFileResponse response = ufsImageClientDefault.getFileMeta(request);

                log.info("cgs --> ufs getFileMeta.response:[{}]",response);

                if(response == null || ApplyStatusEnum.SUCCESS != response.getApplyStatus()) {
                    throw new GatewayFailException(CgsReturnCode.QUERY_FILE_ERROR);
                }

                if(response.getMetaData() != null &&
                        !StringUtils.equals(memberId,response.getMetaData().get("memberId"))) {
                    throw new GatewayFailException(GatewayReturnCode.UNAUTHORIZED, FILE_NO_PERMISSION);
                }

            });
        } catch (Throwable e) {
            log.error("ufs call fail query:",e);
            throw new GatewayFailException(CgsReturnCode.UFS_CALL_FAIL);
        }
    }
}
