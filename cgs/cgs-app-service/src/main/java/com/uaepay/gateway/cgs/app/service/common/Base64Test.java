package com.uaepay.gateway.cgs.app.service.common;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.nio.charset.Charset;

/**
 * <p>描述</p>
 *
 * @author Yadong
 * @version $ Id: Base64Test, v 0.1 2019/12/1 ranber Exp $
 */
public class Base64Test {

    public static void main(String []args){
        String randoms = Base64.toBase64String(SecureRandoms.getInstance().genBytes(16));
        System.out.println(randoms);

        BASE64Encoder base64encoder = new BASE64Encoder();
        System.out.println(base64encoder.encodeBuffer(randoms.getBytes()));
//        System.out.println(new String(Base64.decode(randoms.getBytes()), Charset.forName("UTF-8")));
    }

}
