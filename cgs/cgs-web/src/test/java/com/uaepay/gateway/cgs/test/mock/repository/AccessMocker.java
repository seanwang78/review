package com.uaepay.gateway.cgs.test.mock.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;

@Service
public class AccessMocker {

    Map<String, MockAccess> accessTokenMap = new HashMap<>();
    Map<String, MockAccess> memberIdMap = new HashMap<>();

    public MockAccess mock(MockAccess mockAccess) {
        String accessToken = mockAccess.getAccessToken();
        String memberId = mockAccess.getMemberId();
        System.out.printf("mock access: token=%s, memberId=%s, content=%s\n", accessToken, memberId, mockAccess);
        accessTokenMap.put(accessToken, mockAccess);
        memberIdMap.put(memberId, mockAccess);
        return mockAccess;
    }

    public AccessTokenResponse getMockResponseByAccessToken(String accessToken) {
        return accessTokenMap.containsKey(accessToken) ? accessTokenMap.get(accessToken).getResponse() : null;
    }

    public MockAccess getByMemberId(String memberId) {
        return memberIdMap.get(memberId);
    }

    public void clear() {
        accessTokenMap.clear();
    }

}
