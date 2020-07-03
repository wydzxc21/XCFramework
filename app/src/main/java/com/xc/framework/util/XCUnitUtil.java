package com.xc.framework.util;

import android.content.Context;

/**
 * @author ZhangXuanChen
 * @date 2015-11-23
 * @package com.xc.framework.utils
 * @description 单位转换工具类
 */
public class XCUnitUtil {
	/**
	 * 将dip转换为px
	 *
	 * @param context  上下文
	 * @param dipValue dip值
	 * @return px值
	 */
	public static int getDipToPx(Context context, float dipValue) {
		try {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f * (dipValue >= 0 ? 1 : -1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 将px转换为dip
	 *
	 * @param context 上下文
	 * @param pxValue px值
	 * @return dip值
	 */
	public static int getPxToDip(Context context, float pxValue) {
		try {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f * (pxValue >= 0 ? 1 : -1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 将百分比宽转换为px
	 *
	 * @param context      上下文
	 * @param percentWidth 百分比宽值
	 * @return px值
	 */
	public static int getPercentWidthToPx(Context context, float percentWidth) {
		try {
			final float scale = context.getResources().getDisplayMetrics().widthPixels;
			return (int) (scale * percentWidth / 100f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 将百分比高转换为px
	 *
	 * @param context      上下文
	 * @param percentHeight 百分比高值
	 * @return px值
	 */
	public static int getPercentHeightToPx(Context context, float percentHeight) {
		try {
			final float scale = context.getResources().getDisplayMetrics().heightPixels;
			return (int) (scale * percentHeight / 100f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * php时间戳转java时间戳
	 *
	 * @param phpTimestamp php时间戳(十位)
	 * @return java时间戳
	 */
	public static String getPhpToJavaTimestamp(String phpTimestamp) {
		if (phpTimestamp != null && !"".equals(phpTimestamp) && !"null".equals(phpTimestamp)) {
			if (phpTimestamp.length() < 10) {
				phpTimestamp = phpTimestamp + "0";
			}
			//
			if (phpTimestamp.startsWith("1")) {
				phpTimestamp = phpTimestamp + "000";
			} else {
				phpTimestamp = phpTimestamp + "00";
			}
			String trim = phpTimestamp.replaceAll(" ", "");
			return trim;
		}
		return phpTimestamp;
	}
}
