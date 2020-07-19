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
     * @description 转16进制字符串
     */
    public static String toHexStr(byte b) {
        return toHexStr(new byte[]{b});
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 转16进制字符串
     */
    public static String toHexStr(byte[] bytes) {
        return toHexStr(bytes, false);
    }

    /**
     * @param isSpace 是否加空格
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 转16进制字符串
     */
    public static String toHexStr(byte[] bytes, boolean isSpace) {
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
     * @description 转10进制字符串
     */
    public static String toDecStr(byte b) {
        return toDecStr(new byte[]{b});
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 转10进制字符串
     */
    public static String toDecStr(byte[] bytes) {
        return XCStringUtil.toDecStr(toHexStr(bytes), false);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 转long
     */
    public static long toLong(byte b) {
        long n = b;
        if (n < 0) {
            n = (n & 0x7F) + 0x80;
        }
        return n;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 转int
     */
    public static int toInt(byte b) {
        return (int) toLong(b);
    }

    /**
     * 将字节数组转换为Bitmap
     *
     * @param bytes 字节数组
     * @return bitmap对象
     */
    public static Bitmap toBitmap(byte[] bytes) {
        Bitmap bitmap = null;
        if (bytes != null && bytes.length > 0) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bitmap;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/9 9:49
     * Description：转Base64
     */
    public static String toBase64(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return "";
        }
        String base64Str = new String(Base64.encodeBase64(bytes));
        base64Str = base64Str.replaceAll("\\+", "-").replaceAll("/", "_").replaceAll("%", "_").replaceAll("=", "");
        return base64Str;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/19 10:53
     * Description：低位在前
     */
    public static class LittleEndian {
        /**
         * Author：ZhangXuanChen
         * Time：2020/7/19 10:46
         * Description：转int
         */
        public static int toInt(byte[] bytes) {
            if (bytes == null || bytes.length <= 0) {
                return 0;
            }
            return bytes[0] & 0xFF |
                    (bytes[1] & 0xFF) << 8 |
                    (bytes[2] & 0xFF) << 16 |
                    (bytes[3] & 0xFF) << 24;
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/19 10:54
     * Description：高位在前
     */
    public static class BigEndian {
        /**
         * Author：ZhangXuanChen
         * Time：2020/7/19 10:46
         * Description：转int
         */
        public static int toInt(byte[] bytes) {
            if (bytes == null || bytes.length <= 0) {
                return 0;
            }
            return (bytes[0] & 0xFF) << 24 |
                    (bytes[1] & 0xFF) << 16 |
                    (bytes[2] & 0xFF) << 8 |
                    bytes[3] & 0xFF;
        }
    }

}
