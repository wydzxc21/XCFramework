package com.xc.framework.util;

import android.content.Context;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author ZhangXuanChen
 * @date 2015-11-23
 * @package com.xc.framework.utils
 * @description Toast工具类
 */
public class XCToastUtil {
    /**
     * 显示
     *
     * @param context 上下文
     * @param message 内容信息
     */
    public static void show(Context context, String message) {
        show(context, message, -1, -1, -1, -1);
    }

    /**
     * 显示
     *
     * @param context 上下文
     * @param message 内容信息
     * @param size    dip
     */
    public static void show(Context context, String message, int size) {
        show(context, message, -1, size, -1, -1);
    }

    /**
     * 显示
     *
     * @param context 上下文
     * @param message 内容信息
     * @param gravity 例:Gravity.CENTER
     * @param size    dip
     */
    public static void show(Context context, String message, int size, int gravity) {
        show(context, message, -1, size, -1, gravity);
    }

    //-----------------------------id--------------------------------

    /**
     * 显示
     *
     * @param context   上下文
     * @param messageId 例:R.string.test
     */
    public static void show(Context context, int messageId) {
        show(context, null, messageId, -1, -1, -1);
    }

    /**
     * 显示
     *
     * @param context   上下文
     * @param messageId 例:R.string.test
     * @param sizeId    例:R.dimen.size
     */
    public static void show(Context context, int messageId, int sizeId) {
        show(context, null, messageId, -1, sizeId, -1);
    }

    /**
     * 显示
     *
     * @param context   上下文
     * @param messageId 例:R.string.test
     * @param sizeId    例:R.dimen.size
     * @param gravity   例:Gravity.CENTER
     */
    public static void show(Context context, int messageId, int sizeId, int gravity) {
        show(context, null, messageId, -1, sizeId, gravity);
    }

    /**
     * 显示
     *
     * @param context   上下文
     * @param message   内容信息
     * @param messageId 例:R.string.test
     * @param size      dip
     * @param sizeId    例:R.dimen.size
     * @param gravity   例:Gravity.CENTER
     */
    public static synchronized void show(Context context, String message, int messageId, int size, int sizeId, int gravity) {
        try {
            if (context != null) {
                Toast toast = null;
                if (!XCStringUtil.isEmpty(message)) {
                    toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
                } else if (messageId > 0) {
                    toast = Toast.makeText(context.getApplicationContext(), messageId, Toast.LENGTH_SHORT);
                }
                //
                if (size > 0 || sizeId > 0) {
                    LinearLayout layout = (LinearLayout) toast.getView();
                    TextView tv = (TextView) layout.getChildAt(0);
                    if (size > 0) {
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
                    } else if (sizeId > 0) {
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, context.getResources().getDimensionPixelSize(sizeId));
                    }
                }
                //
                if (gravity > 0) {
                    toast.setGravity(gravity, 0, 0);
                }
                toast.show();
            }
        } catch (Exception e) {
        }
    }
}
