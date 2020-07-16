package com.xc.framework.util;

import java.lang.reflect.Array;

/**
 * Date：2020/7/16
 * Author：ZhangXuanChen
 * Description：数组工具
 */
public class XCArrayUtil {

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/16 18:43
     * Description：数组截取
     * Param：skip 跳过索引
     * Param：take 截取长度
     */
    public static <T> T copyOf(T original, int skip, int take) {
        if (original == null || skip < 0 || take < 0) {
            return null;
        }
        T copy = (T) Array.newInstance(original.getClass().getComponentType(), take);
        System.arraycopy(original, skip, copy, 0, take);
        return copy;
    }

}
