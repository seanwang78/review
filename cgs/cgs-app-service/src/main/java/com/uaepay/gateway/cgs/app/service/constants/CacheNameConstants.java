package com.uaepay.gateway.cgs.app.service.constants;

public interface CacheNameConstants {

    /** 登录令牌，关联用户的ACCESS_TOKEN */
    String LOGIN_TOKEN = "LOGIN_TOKEN:";

    /** 访问令牌，关联用户信息 */
    String ACCESS_TOKEN = "ACCESS_TOKEN:";

    /** 盐 */
    String SALT = "SALT:";

    /** App 升级 20200504修改了缓存的数据结构 更换缓存key 否则可能会出现类转换异常的错误 */
    String APP_UPGRADE = "APP_UPGRADE";
}
