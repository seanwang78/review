package com.uaepay.gateway.cgs.integration.member;

public interface MaPayPasswordService {

    String decryptPayPassword(String paypwd, String salt);

}
