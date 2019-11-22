package com.xc.framework.util;

import android.content.Context;
import android.support.compat.BuildConfig;
import android.util.Log;

/**
 * @author ZhangXuanChen
 * @date 2015-11-20
 * @package com.xc.framework.utils
 * @description Log日志工具类
 */
public class XCLogUtil {
	/**
	 * Log.i
	 * 
	 * @deprecated  tag为当前类名
	 * @param msg 打印信息
	 */
	public static void i(Context context, String msg) {
		try {
			if (BuildConfig.DEBUG) {
				Log.i("" + context.getClass().getSimpleName(), msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Log.d
	 * 
	 * @deprecated tag为当前类名
	 * @param msg 打印信息
	 */
	public static void d(Context context, String msg) {
		try {
			if (BuildConfig.DEBUG) {
				Log.d("" + context.getClass().getSimpleName(), msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Log.e
	 * 
	 * @deprecated tag为当前类名
	 * @param msg 打印信息
	 */
	public static void e(Context context, String msg) {
		try {
			if (BuildConfig.DEBUG) {
				Log.e("" + context.getClass().getSimpleName(), msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
