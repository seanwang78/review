package com.uaepay.gateway.cgs.app.service.common;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * cgs 扩展错误码 .
 *
 * <p>
 *
 * @author yusai
 * @date 2020-01-15 15:47.
 */
public enum CgsReturnCode implements CodeMessageEnum {

    DEVICEIDNOTMATCH("403", "${cgs.deviceid_not_match}"),
    REQUEST_CHECK_CONFIG_ERROR("400", "${cgs.request_check_config}"),
    UFS_CALL_FAIL("500","${cgs.ufs_call_fail}"),
    UPLOAD_FILE_ERROR("500","${cgs.upload_file_error}"),
    MAX_UPLOAD_SIZE_EXCEEDED("413","${cgs.max_upload_size_exceeded}"),
    QUERY_FILE_ERROR("500","${cgs.query_file_error"),
    MEMBER_RSA_NOT_FOUND("500","${cgs.member_rsa_not_found"),
    SALT_EXPIRED("400", "${gateway.saltExpired}"),
    TOKEN_ISSUER_ERROR("400", "${gateway.token_issuer_error}"),
    APP_VERSION_UPGRADE("505","${cgs.app_version_upgrade}"),
    API_NOT_SUPPORT("605","${cgs.api_not_support}"),
    UPGRADE_APP_TO_VERIFY_KYC("500", "${cgs.upgrade_app_to_verify_kyc}"),
    MEMBER_NOT_SAME_ERR("55001", "${cgs.member_not_same}"),
    DECRYPT_PAYPWD_ERROR("55002", "${payPwdVerifyFail}"),
    RPC_EXCEPTION("55500", "${cgs.rpc_exception}"),
    GUARD_INTERCEPT("609","${cgs.guard_intercept}"),

    ;

    private final String code;
    private final String message;

    CgsReturnCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }
}
