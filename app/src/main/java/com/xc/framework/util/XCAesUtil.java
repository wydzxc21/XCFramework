package com.xc.framework.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author：ZhangXuanChen
 * Time：2021/4/15 8:43
 * Description：Aes加密
 */
public class XCAesUtil {
    public static final int Base64 = 1;
    public static final int HexStr = 2;

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/15 11:26
     * Description：加密
     *
     * @param content  内容
     * @param password 密码（必须16位）
     */
    public static String encrypt(String content, String password) {
        return encrypt(content, password, XCAesUtil.Base64);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/15 11:26
     * Description：加密
     *
     * @param content    内容
     * @param password   密码（必须16位）
     * @param outputType 输出类型
     */
    public static String encrypt(String content, String password, int outputType) {
        try {
            if (XCStringUtil.isEmpty(content) || XCStringUtil.isEmpty(password) || outputType < 0) {
                return "";
            }
            if (password.getBytes("GBK").length != 16) {
                return "";
            }
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, password);
            byte[] bytes = cipher.doFinal(content.getBytes());
            if (outputType == XCAesUtil.Base64) {
                return XCByteUtil.toBase64(bytes);
            } else {
                return XCByteUtil.toHexStr(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/15 11:28
     * Description：解密
     *
     * @param content  内容
     * @param password 密码（必须16位）
     */
    public static String decrypt(String content, String password) {
        return decrypt(content, password, XCAesUtil.Base64);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/15 11:28
     * Description：解密
     *
     * @param content    内容
     * @param password   密码（必须16位）
     * @param outputType 输出类型
     */
    public static String decrypt(String content, String password, int outputType) {
        try {
            if (XCStringUtil.isEmpty(content) || XCStringUtil.isEmpty(password) || outputType < 0) {
                return "";
            }
            if (password.getBytes("GBK").length != 16) {
                return "";
            }
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, password);
            byte[] bytes = null;
            if (outputType == XCAesUtil.Base64) {
                bytes = cipher.doFinal(XCBase64Util.toBytes(content));
            } else {
                bytes = cipher.doFinal(XCStringUtil.toBytes(content));
            }
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/15 11:37
     * Description：getCipher
     */
    private static Cipher getCipher(int mode, String password) {
        byte[] iv = new byte[]{-12, 35, -25, 65, 45, -87, 95, -22, -15, 45, 55, -66, 32, 1, 84, 55};//16位
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec sks = new SecretKeySpec(password.getBytes(), "AES");
            IvParameterSpec ips = new IvParameterSpec(iv);
            c.init(mode, sks, ips);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
