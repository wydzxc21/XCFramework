package com.xc.framework.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Date：2021/3/24
 * Author：ZhangXuanChen
 * Description：字体工具
 */
public class TypefaceUtil {
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/24 9:41
     * Description：get
     * assetsPath: fonts/SourceHanSansCN-Medium.ttf
     */
    public static Typeface get(Context context, String assetsPath) {
        if (context == null || XCStringUtil.isEmpty(assetsPath)) {
            return null;
        }
        Typeface tf = fontCache.get(assetsPath);
        if (tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), assetsPath);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(assetsPath, tf);
        }
        return tf;
    }
}
