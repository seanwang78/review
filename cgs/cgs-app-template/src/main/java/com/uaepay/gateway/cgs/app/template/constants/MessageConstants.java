package com.uaepay.gateway.cgs.app.template.constants;

/**
 * 消息常量
 * @author zc
 */
public interface MessageConstants {

    String REQUEST_BODY_FORMAT_ERROR = "${requestBodyFormatError}";

    /** 当前请求设备跟访问Token中的设备不一致 */
    String DEVICE_NO_MATCH = "${cgs.device_no_match}";

    /** 当前请求主机App与accessToken中的不一致 */
    String HOST_APP_NO_MATCH = "${cgs.host_app_no_match}";

    /** 无权操作该文件 */
    String FILE_NO_PERMISSION = "${cgs.file_no_permission}";
}
