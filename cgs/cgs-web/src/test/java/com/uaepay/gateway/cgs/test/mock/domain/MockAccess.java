package com.uaepay.gateway.cgs.test.mock.domain;

import com.uaepay.basis.beacon.common.util.UuidUtil;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import lombok.Data;

@Data
public class MockAccess {

    public static final String MOCK_IDENTITY = "mockIdentity";
    public static final String MOCK_MEMBER_ID = "mockMember";

    AccessTokenResponse response = new AccessTokenResponse();
    boolean passMemberPermissionCheck = true;

    public MockAccess() {
        this(MOCK_IDENTITY, PlatformType.Android.getCode(), MOCK_MEMBER_ID);
    }

    public MockAccess(String identity) {
        this(identity, PlatformType.Android.getCode(), identity);
    }

    public MockAccess(String identity, String platform, String memberId) {
        response.setIdentity(identity);
        response.setMemberId(memberId);
        response.setAccessToken(UuidUtil.generate());
        response.setAccessKey(UuidUtil.generate());
    }

    public MockAccess squeezeOut() {
        response.setSqueezeOut(YesNoEnum.YES);
        return this;
    }

    public MockAccess deviceId(String deviceId) {
        response.setDeviceId(deviceId);
        return this;
    }

    public MockAccess memberPermissionCheck(boolean pass) {
        passMemberPermissionCheck = pass;
        return this;
    }

    public String getAccessToken() {
        return response.getAccessToken();
    }

    public String getAccessKey() {
        return response.getAccessKey();
    }

    public String getMemberId() {
        return response.getMemberId();
    }

    public boolean isPassMemberPermissionCheck() {
        return passMemberPermissionCheck;
    }

    public AccessTokenResponse getResponse() {
        return response;
    }

}
