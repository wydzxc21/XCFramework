package com.xc.framework.util;

/**
 * Date：2020/7/19
 * Author：ZhangXuanChen
 * Description：int工具
 */
public class XCIntUtil {
    /**
     * Author：ZhangXuanChen
     * Time：2020/7/19 10:53
     * Description：低位在前
     */
    public static class LittleEndian {
        /**
         * Author：ZhangXuanChen
         * Time：2020/7/19 10:46
         * Description：转字节数组
         */
        public static byte[] toBytes(int value) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (value & 0xFF);
            bytes[1] = (byte) ((value >> 8) & 0xFF);
            bytes[2] = (byte) ((value >> 16) & 0xFF);
            bytes[3] = (byte) ((value >> 24) & 0xFF);
            return bytes;
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
         * Description：转字节数组
         */
        public static byte[] toBytes(int value) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) ((value >> 24) & 0xFF);
            bytes[1] = (byte) ((value >> 16) & 0xFF);
            bytes[2] = (byte) ((value >> 8) & 0xFF);
            bytes[3] = (byte) (value & 0xFF);
            return bytes;
        }
    }
}
