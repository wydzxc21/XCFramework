package com.xc.framework.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.commons.codec.binary.Base64;

/**
 * @author ZhangXuanChen
 * @date 2020/2/8
 * @package com.xc.framework.util
 * @description 字节工具
 */
public class XCByteUtil {

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 字节数组转16进制字符串
     */
    public static String byteToHexStr(byte[] bytes) {
        return byteToHexStr(bytes, false);
    }

    /**
     * @param isSpace 是否加空格
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 字节数组转16进制字符串
     */
    public static String byteToHexStr(byte[] bytes, boolean isSpace) {
        if (bytes == null || bytes.length <= 0) {
            return "";
        }
        char[] hexChar = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(hexChar[(b & 0xFF) >> 4]);
            sb.append(hexChar[b & 0x0F]);
            if (isSpace) {
                sb.append(" ");
            }
        }
        if (isSpace) {
            return sb.substring(0, sb.length() - 1);
        } else {
            return sb.toString();
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 字节数组转10进制字符串
     */
    public static String byteToDecStr(byte[] bytes) {
        return XCStringUtil.hexStrToDecStr(byteToHexStr(bytes), false);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 字节转16进制字符串
     */
    public static String byteToHexStr(byte b) {
        return byteToHexStr(new byte[]{b});
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 字节转10进制字符串
     */
    public static String byteToDecStr(byte b) {
        return byteToDecStr(new byte[]{b});
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 字节转long
     */
    public static long byteToLong(byte b) {
        long n = b;
        if (n < 0) {
            n = (n & 0x7F) + 0x80;
        }
        return n;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 字节转int
     */
    public static int byteToInt(byte b) {
        return (int) byteToLong(b);
    }

    /**
     * 将字节数组转换为Bitmap
     *
     * @param bytes 字节数组
     * @return bitmap对象
     */
    public static Bitmap byteToBitmap(byte[] bytes) {
        Bitmap bitmap = null;
        if (bytes != null && bytes.length > 0) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bitmap;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/9 9:49
     * Description：字节数组转Base64
     */
    public static String byteToBase64(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return "";
        }
        String base64Str = new String(Base64.encodeBase64(bytes));
        base64Str = base64Str.replaceAll("\\+", "-").replaceAll("/", "_").replaceAll("%", "_").replaceAll("=", "");
        return base64Str;
    }

}
