package com.xc.framework.util;

import android.content.Context;
import android.graphics.Color;

/**
 * @author ZhangXuanChen
 * @date 2016-11-13
 * @package com.xc.framework.utils
 * @description 颜色工具类
 */
public class XCColorUtil {
	/**
	 * 获取将16进制颜色转换为资源的颜色
	 * 
	 * @param hex
	 *            例:#ffffffff
	 * @return 资源地址
	 */
	public static int getHexToColor(String hex) {
		if (!XCStringUtil.isEmpty(hex)) {
			int color = (int) Long.parseLong(hex.replace("#", ""), 16);
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = (color >> 0) & 0xFF;
			return Color.rgb(r, g, b);
		}
		return 0;
	}

	/**
	 * 获取资源中的颜色
	 * 
	 * @param context 上下文
	 * @param color
	 *            例:R.id.color
	 * @return 色值
	 */
	public static int getResourcesColor(Context context, int color) {
		if (context != null && color >= 0) {
			return context.getResources().getColor(color);
		}
		return 0;
	}

	/**
	 * 获取修改透明度的颜色
	 * 
	 * @param color 例:R.id.color
	 * @param alpha 透明度
	 * @return 色值
	 */
	public static int getChangeAlpha(int color, int alpha) {
		if (color >= 0 && alpha >= 0) {
			int red = Color.red(color);
			int green = Color.green(color);
			int blue = Color.blue(color);
			return Color.argb(alpha, red, green, blue);
		}
		return 0;
	}
}
