package com.uaepay.gateway.cgs.test.mock.impl;

import com.uaepay.gateway.cgs.test.mock.repository.AccessMocker;
import com.uaepay.pts.ext.facade.AccessTokenFacade;
import com.uaepay.pts.ext.facade.request.*;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import com.uaepay.pts.ext.facade.response.Response;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import static com.uaepay.basis.beacon.common.util.UuidUtil.generate;

@Service
public class MockAccessTokenFacade implements AccessTokenFacade {
    public MockAccessTokenFacade() {
        System.out.println("dsa");
    }
    @Autowired
    AccessMocker accessMocker;

    @Override
    public AccessTokenResponse getAccessToken(AccessTokenQueryRequest accessTokenQueryRequest) {
        return accessMocker.getMockResponseByAccessToken(accessTokenQueryRequest.getAccessToken());
    }

    @Override
    public AccessTokenResponse getAccessTokenV2(AccessTokenQueryRequest query) {
        return accessMocker.getMockResponseByAccessToken(query.getAccessToken());
    }

    @Override
    public AccessTokenResponse getAccessTokenByUser(AccessTokenQueryByUserRequest request) {
        return null;
    }

    @Override
    public AccessTokenResponse silentLogin(SilentLoginRequest silentLoginRequest) {
        return null;
    }

    @Override
    public AccessTokenResponse applyAccessToken(ApplyTokenRequest request) {
        AccessTokenResponse response = new AccessTokenResponse();
        response.setMemberId(request.getMemberId());
        response.setDeviceId(request.getDeviceId());
        response.setHasPayPwd(request.getHasPayPwd());
        response.setHasRealName(request.getHasRealName());
        response.setPartnerId(request.getPartnerId());
        response.setAccessToken(generate());
        response.setAccessKey(generate());
        return response;
    }

    @Override
    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest request) {
        return null;
    }

    @Override
    public AccessTokenResponse updateAccessToken(UpdateAccessTokenRequest request) {
        return null;
    }

    @Override
    public Response logOutByMemberId(LogOutRequest logOutRequest) {
        return null;
    }
}
