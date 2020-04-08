package com.xc.framework.util;

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
        char[] hexChar = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(hexChar[(b & 0xFF) >> 4]);
            sb.append(hexChar[b & 0x0F]);
            sb.append(" ");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 字节数组转10进制字符串
     */
    public static String byteToDecStr(byte[] bytes) {
        return XCStringUtil.hexStrToDecStr(byteToHexStr(bytes));
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

}
