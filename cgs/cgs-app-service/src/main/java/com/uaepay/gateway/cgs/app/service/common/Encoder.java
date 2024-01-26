package com.uaepay.gateway.cgs.app.service.common;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>描述</p>
 *
 * @author Yadong
 * @version $ Id: Encoder, v 0.1 2019/12/1 ranber Exp $
 */
public interface Encoder {

    int encode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException;

    int decode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException;

    int decode(String var1, OutputStream var2) throws IOException;

}
