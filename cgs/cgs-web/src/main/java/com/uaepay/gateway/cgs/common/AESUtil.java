package com.uaepay.gateway.cgs.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

/**
 * <p>AES-128-CBC</p>
 *
 * @author Yadong
 * @version $ Id: AESUtil, v 0.1 2022/5/3 ranber Exp $
 */
@Slf4j
public class AESUtil {

    public static final String ALGORITHM = "AES/CBC/PKCS7Padding";

    public static final String AES_NAME = "AES";

    private static final int MAX_ENCRYPT_BLOCK = 117;

    private static final int MAX_DECRYPT_BLOCK = 128;

    public static SecretKeySpec skeySpec = null;

    public static AlgorithmParameterSpec ivSpec = null;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public AESUtil init(String key, String iv){
        ivSpec = new IvParameterSpec(toByteArray(iv));
        skeySpec = new SecretKeySpec(toByteArray(key), AES_NAME);
        return this;
    }

    /**
     * 根据密码进行文件内容加密
     *
     * @param value    文件内容
     * @return 先对前端输入的 md5 加密 生成32位字符串
     * @throws Exception
     */
    public String encrypt(String value) throws Exception{
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] data = value.getBytes("utf-8");
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.encodeBase64String(encryptedData);
    }


    public String decrypt(String encrypted) throws Exception{
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);

        byte[] encryptedData = Base64.decodeBase64(encrypted);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] original = out.toByteArray();
        out.close();
        return new String(original);
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] toByteArray(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    public static void main(String[] args) throws Exception {
        //P3234171,P3234173
        AESUtil util = new AESUtil().init("02006cb101c0d6c238739b5638b081fc", "ceb95fe9d08cbe9e02c992ec8d1b4bbc");
        String originalString = "6264747688957";
        System.out.println("Original String to encrypt:" + originalString);
        String encryptedString = util.encrypt(originalString);
        System.out.println("Encrypted String:" + encryptedString);
        System.out.println(util.decrypt(encryptedString));
    }

}
