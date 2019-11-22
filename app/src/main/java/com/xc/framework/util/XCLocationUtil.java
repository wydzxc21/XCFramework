package com.xc.framework.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

/**
 * @author ZhangXuanChen
 * @date 2015-11-22
 * @package com.xc.framework.utils
 * @description 定位相关工具类
 */
public class XCLocationUtil {
	/**
	 * 是否开启GPS
	 * 
	 * @param context 上下文
	 * @return 是否开启
	 */
	public static boolean isOpenGPS(Context context) {
		LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (!alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		}
		return true;
	}

	/**
	 * 强制开启GPS
	 * 
	 * @param context 上下文
	 */
	public static final void openGPS(Context context) {
		try {
			if (!isOpenGPS(context)) {
				Intent GPSIntent = new Intent();
				GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
				GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
				GPSIntent.setData(Uri.parse("custom:3"));
				PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据两点经纬度，计算直线距离多少米
	 * 
	 * @param startLongitude
	 *            ：起始经度
	 * @param startLatitude
	 *            ：起始纬度
	 * @param endLongitude
	 *            ：终点经度
	 * @param endLatitude
	 *            ：终点纬度
	 * @return 结果值
	 */
	public static double getDistance(String startLongitude, String startLatitude, String endLongitude, String endLatitude) {
		if (startLongitude != null && !"".equals(startLongitude) && startLatitude != null && !"".equals(startLatitude) && endLongitude != null && !"".equals(endLongitude) && endLatitude != null && !"".equals(endLatitude)) {
			try {
				double lng1 = Double.parseDouble(startLongitude);
				double lat1 = Double.parseDouble(startLatitude);
				double lng2 = Double.parseDouble(endLongitude);
				double lat2 = Double.parseDouble(endLatitude);
				if (lng1 > lat1 && lng2 > lat2) {
					double radLat1 = lat1 * Math.PI / 180;
					double radLat2 = lat2 * Math.PI / 180;
					double a = radLat1 - radLat2;
					double b = lng1 * Math.PI / 180 - lng2 * Math.PI / 180;
					double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
					s = s * 6378137;
					s = Math.round(s * 10000) / 10000;// 单位(m)
					return s;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}
