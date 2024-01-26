package com.uaepay.gateway.cgs.app.service.domain;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/5/29
 * @since 0.1
 */
public interface UpgradeLevel{
    /** 不更新 */
    Integer NONE = 0;
    /** 强制更新 */
    Integer FORCE = 1;
    /** 非强制更新 */
    Integer TIP = 2;
}
