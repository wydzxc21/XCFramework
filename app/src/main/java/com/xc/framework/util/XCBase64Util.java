package com.xc.framework.util;


import android.graphics.Bitmap;

import org.apache.commons.codec.binary.Base64;

/**
 * @author ZhangXuanChen
 * @date 2020/3/2
 * @package com.xc.framework.util
 * @description Base64工具
 */
public class XCBase64Util {

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/9 9:53
     * Description：转字节数组
     */
    public static byte[] toBytes(String base64Str) {
        if (XCStringUtil.isEmpty(base64Str)) {
            return null;
        }
        return Base64.decodeBase64(base64Str.getBytes());
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/9 9:53
     * Description：转字符串
     */
    public static String toStr(String base64Str) {
        if (XCStringUtil.isEmpty(base64Str)) {
            return "";
        }
        return new String(Base64.decodeBase64(base64Str.getBytes()));
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/9 11:53
     * Description：转bitmap
     */
    public static Bitmap toBitmap(String base64Str) {
        if (XCStringUtil.isEmpty(base64Str)) {
            return null;
        }
        return XCByteUtil.toBitmap(toBytes(base64Str));
    }
}
