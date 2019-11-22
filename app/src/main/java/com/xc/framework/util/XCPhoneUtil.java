package com.xc.framework.util;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;

/**
 * @author ZhangXuanChen
 * @date 2015-11-22
 * @package com.xc.framework.utils
 * @description 手机相关工具类
 */
public class XCPhoneUtil {
	/**
	 * 获取设备名称
	 * 
	 * @return 手机设备名称
	 */
	public static String getDeviceName() {
		return Build.DEVICE;
	}

	/**
	 * 获取设备型号
	 * 
	 * @return 手机设备型号
	 */
	public static String getDeviceModel() {
		return Build.MODEL;
	}

	/**
	 * 获取当前系统的android版本号
	 * 
	 * @return 手机操作系统的版本号
	 */
	public static int getSystemVersion() {
		return VERSION.SDK_INT;
	}

	/**
	 * 获取手机IMEI码（国际移动设备身份码，它与每台移动电话机一一对应，而且该码是全世界唯一的）
	 * 
	 * @param context 上下文
	 * @return 手机IMEI码
	 */
	public static String getIMEICode(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * 获取手机IMSI码（国际移动用户识别码，储存在SIM卡中，可用于区别移动用户的有效信息）
	 * 
	 * @param context
	 *            上下文
	 * @return 手机IMSI码
	 */
	public static String getIMSICode(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();
	}

	/**
	 * 获取手机号
	 * 
	 * @param context 上下文
	 * @return 手机号
	 */
	public static String getMobileNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String mobile = telephonyManager.getLine1Number();
		if (!XCStringUtil.isEmpty(mobile) && mobile.startsWith("+86")) {
			mobile = mobile.substring("+86".length());
		}
		return mobile;
	}
}
