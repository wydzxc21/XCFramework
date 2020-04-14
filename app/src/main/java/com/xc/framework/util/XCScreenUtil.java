package com.xc.framework.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * @author ZhangXuanChen
 * @date 2017-1-22
 * @package com.xc.framework.utils
 * @description 屏幕相关工具类
 */
public class XCScreenUtil {
    /**
     * 获取屏幕宽
     *
     * @param context 上下文
     * @return 屏幕宽
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高
     *
     * @param context 上下文
     * @return 屏幕高
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取状态栏高
     *
     * @param context 上下文
     * @return 状态栏高
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/14 16:30
     * Description：全屏
     */
    public static void fullScreen(Activity activity) {
        hideStatusBar(activity);
        hideVirtualButton(activity);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/14 15:54
     * Description：隐藏状态栏
     */
    public static void hideStatusBar(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/14 16:02
     * Description：隐藏虚拟按键
     */
    public static void hideVirtualButton(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
    }

    /**
     * 获取标题栏高
     *
     * @param activity 上下文
     * @return 标题栏高
     */
    public static int getTitleBarHeight(Activity activity) {
        int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return contentTop - getStatusBarHeight(activity);
    }

    /**
     * 显示输入法
     *
     * @param context 上下文
     */
    public static void showInputKeyboard(Context context) {
        try {
            View view = ((Activity) context).getWindow().peekDecorView();
            if (view != null && view.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                imm.showSoftInput(view, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏输入法
     *
     * @param context 上下文
     */
    public static void hideInputKeyboard(Context context) {
        try {
            View view = ((Activity) context).getWindow().peekDecorView();
            if (view != null && view.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
