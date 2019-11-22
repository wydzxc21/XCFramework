package com.xc.framework.util;

import android.app.Activity;
import android.content.Context;
import android.view.Window;

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
	 * 获取标题栏高
	 * 
	 * @param activity 上下文
	 * @return 标题栏高
	 */
	public static int getTitleBarHeight(Activity activity) {
		int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		return contentTop - getStatusBarHeight(activity);
	}
}
