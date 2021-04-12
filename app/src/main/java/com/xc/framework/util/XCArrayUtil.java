package com.xc.framework.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/9 14:21
     * Description：分割集合
     * Param：list 集合
     * Param：length 分割长度
     */
    public static <T> List<List<T>> split(List<T> list, int length) {
        if (list == null || list.isEmpty() || length <= 0) {
            return null;
        }
        if (length >= list.size()) {
            return null;
        }
        List<List<T>> mList = new ArrayList<List<T>>();
        int listSize = (int) Math.ceil((float) list.size() / (float) length);
        int from, to;
        for (int i = 0; i < listSize; i++) {
            from = i * length;
            to = from + length;
            if (to > list.size()) {
                to = list.size();
            }
            List<T> splitList = new ArrayList<T>();
            for (int j = from; j < to; j++) {
                splitList.add(list.get(j));
            }
            mList.add(splitList);
        }
        return mList;
    }

}
