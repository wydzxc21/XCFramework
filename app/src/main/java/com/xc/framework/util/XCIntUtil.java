package com.xc.framework.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Date：2020/7/19
 * Author：ZhangXuanChen
 * Description：int工具
 */
public class XCIntUtil {

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/25 9:30
     * Description：转数组
     * Param：value int
     * Param：byteOrder 高低位
     */
    public static byte[] toBytes(int value, ByteOrder byteOrder) {
        return toBytes(value, 4, byteOrder);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/25 9:30
     * Description：转数组
     * Param：value int
     * Param：allocate 分配空间
     * Param：byteOrder 高低位
     */
    public static byte[] toBytes(int value, int allocate, ByteOrder byteOrder) {
        if (allocate <= 0 || byteOrder == null) {
            return null;
        }//
        ByteBuffer byteBuffer = ByteBuffer.allocate(allocate).order(byteOrder);
        return byteBuffer.putInt(value).array();
    }
}
