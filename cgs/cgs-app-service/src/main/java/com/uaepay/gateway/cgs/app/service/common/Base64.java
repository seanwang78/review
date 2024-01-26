package com.uaepay.gateway.cgs.app.service.common;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>描述</p>
 *
 * @author Yadong
 * @version $ Id: Base64, v 0.1 2019/12/1 ranber Exp $
 */
public class Base64 extends Base64Kit{

    public Base64() {
    }

    public static byte[] encode(byte[] data) {
        return FastBase64.encode(data);
    }

    public static int encode(byte[] data, OutputStream out) throws IOException {
        return Base64Kit.encode(data, 0, data.length, out);
    }

    public static int encode(byte[] data, int off, int length, OutputStream out) throws IOException {
        return Base64Kit.encode(data, off, length, out);
    }

    public static byte[] decode(byte[] data) {
        try {
            return FastBase64.decode(data);
        } catch (IOException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    public static byte[] decode(String base64String) {
        return Base64Kit.decode(base64String);
    }

    public static int decode(String base64String, OutputStream out) throws IOException {
        return Base64Kit.decode(base64String, out);
    }

}
