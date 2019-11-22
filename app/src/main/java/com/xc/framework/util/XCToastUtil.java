package com.xc.framework.util;

import android.content.Context;
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
		showToast(context, message, -1, -1);
	}

	/**
	 * 显示
	 * 
	 * @param context 上下文
	 * @param message 内容信息
	 * @param gravity
	 *            例:Gravity.CENTER
	 */
	public static void show(Context context, String message, int gravity) {
		showToast(context, message, -1, gravity);
	}

	/**
	 * 显示
	 * 
	 * @param context 上下文
	 * @param messageId 例:R.string.test
	 */
	public static void show(Context context, int messageId) {
		showToast(context, null, messageId, -1);
	}

	/**
	 * 显示
	 * 
	 * @param context 上下文
	 * @param messageId 例:R.string.test
	 * @param gravity
	 *            例:Gravity.CENTER
	 */
	public static void show(Context context, int messageId, int gravity) {
		showToast(context, null, messageId, gravity);
	}

	/**
	 * 显示
	 * 
	 * @param context 上下文
	 * @param message 内容信息
	 * @param messageId 例:R.string.test
	 * @param gravity
	 *            例:Gravity.CENTER
	 */
	private synchronized static void showToast(Context context, String message, int messageId, int gravity) {
		try {
			if (context != null) {
				Toast toast = null;
				if (!XCStringUtil.isEmpty(message)) {
					toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
				} else if (messageId != -1) {
					toast = Toast.makeText(context.getApplicationContext(), messageId, Toast.LENGTH_SHORT);
				}
				//
				if (gravity != -1 && gravity > 0) {
					toast.setGravity(gravity, 0, 0);
				}
				toast.show();
			}
		} catch (Exception e) {
		}
	}
}
