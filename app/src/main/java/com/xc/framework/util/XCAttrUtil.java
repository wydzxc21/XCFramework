package com.xc.framework.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Date：2020/6/15
 * Author：ZhangXuanChen
 * Description：Attr工具
 */
public class XCAttrUtil {

    /**
     * Author：ZhangXuanChen
     * Time：2020/6/15 14:23
     * Description：getTypedValue
     */
    public static TypedValue getTypedValue(Context context, int attr) {
        if (context == null || attr <= 0) {
            return null;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/6/15 14:30
     * Description：getResourceId
     */
    public static int getResourceId(Context context, int attr) {
        TypedValue typedValue = getTypedValue(context, attr);
        if (typedValue != null) {
            return typedValue.resourceId;
        }
        return 0;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/6/15 14:30
     * Description：getColor
     */
    public static int getColor(Context context, int attr) {
        TypedValue typedValue = getTypedValue(context, attr);
        if (typedValue != null) {
            return typedValue.data;
        }
        return 0;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/6/15 14:30
     * Description：getDensity
     */
    public static int getDensity(Context context, int attr) {
        TypedValue typedValue = getTypedValue(context, attr);
        if (typedValue != null) {
            return typedValue.density;
        }
        return 0;
    }
}
