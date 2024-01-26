package com.uaepay.gateway.cgs.app.service.common;

import java.security.SecureRandom;

/**
 * <p>描述</p>
 *
 * @author Yadong
 * @version $ Id: SecureRandoms, v 0.1 2019/12/1 ranber Exp $
 */
public final class SecureRandoms {
    SecureRandom random = new SecureRandom();
    private static volatile com.uaepay.gateway.cgs.app.service.common.SecureRandoms instance = new com.uaepay.gateway.cgs.app.service.common.SecureRandoms();

    public static com.uaepay.gateway.cgs.app.service.common.SecureRandoms getInstance() {
        return instance;
    }

    private SecureRandoms() {
    }

    public final SecureRandom newSecureRandom() {
        return new SecureRandom();
    }

    public final byte[] genBytes(int length) {
        byte[] bytes = new byte[length];
        this.random.nextBytes(bytes);
        return bytes;
    }

    public final byte[] genBytesWithoutZero(int length) {
        byte[] bytes = new byte[length];
        this.random.nextBytes(bytes);

        for(int i = 0; i < bytes.length; ++i) {
            while(bytes[i] == 0) {
                bytes[i] = (byte)this.random.nextInt();
            }
        }

        return bytes;
    }
}
