package com.uaepay.gateway.cgs.app.service.domain.response;

import com.uaepay.cms.facade.domain.AppVersionContentInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/5/29
 * @since 0.1
 */
@Data
public class NewVersionResponse implements Serializable {

    /** APP版本名称 */
    private String versionName;

    /** 下载链接 */
    private String downloadUrl;

    /** 下载类型：DIRECT、FORWARD */
    private String downloadType;

    /** App大小 */
    private String appSize;

    /** 图片地址 */
    private String images;

    /** 更新级别 */
    private Integer upgradeLevel;

    /** 内容 */
    private List<AppVersionContentInfo> contents;
}
