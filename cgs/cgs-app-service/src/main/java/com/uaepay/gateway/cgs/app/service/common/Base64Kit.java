package com.uaepay.gateway.cgs.app.service.common;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>描述</p>
 *
 * @author Yadong
 * @version $ Id: Base64Kit, v 0.1 2019/12/1 ranber Exp $
 */
public class Base64Kit {

    public Base64Kit() {
    }

    public static String toBase64String(byte[] data) {
        String var2;
        String base64Text = null;
        if (data == null) {
            base64Text = null;
        } else {
            base64Text = toBase64String(data, 0, data.length);
        }

        var2 = base64Text;

        return var2;
    }

    public static String toBase64String(byte[] data, int off, int length) {
        String var4;
        byte[] encoded = encode(data, off, length);
        var4 = Strings.fromByteArray(encoded);
        return var4;
    }

    public static int encode(byte[] data, int off, int length, OutputStream out) throws IOException {
        int var5;
        Base64Encoder encoder = new Base64Encoder();
        var5 = encoder.encode(data, off, length, out);

        return var5;
    }

    public static byte[] decode(String data) {
        byte[] var12;
        try {
            byte[] returnBytes = null;
            if (data != null) {
                int len = data.length() / 4 * 3;
                ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);
                Base64Encoder encoder = new Base64Encoder();
                encoder.decode(data, bOut);
                returnBytes = bOut.toByteArray();
            } else {
                returnBytes = new byte[0];
            }

            var12 = returnBytes;
        } catch (Exception var9) {
            throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR, "随机数生成失败");
        }

        return var12;
    }

    public static int decode(String data, OutputStream out) throws IOException {

        int var8;
        int returnLength = 0;
        if (data != null && out != null) {
            Base64Encoder encoder = new Base64Encoder();
            returnLength = encoder.decode(data, out);
        }

        var8 = returnLength;

        return var8;
    }

    public static byte[] encode(byte[] data, int off, int length) {

        byte[] var6;
        try {
            int len = (length + 2) / 3 * 4;
            ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);
            Base64Encoder encoder = new Base64Encoder();
            encoder.encode(data, off, length, bOut);
            var6 = bOut.toByteArray();
        } catch (Exception var11) {
//            throw new EncoderException("exception encoding base64 string: " + var11.getMessage(), var11);
            throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR, "随机数生成失败");
        }

        return var6;
    }

}
