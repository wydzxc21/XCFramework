package com.xc.framework.util;

import android.view.View.MeasureSpec;

/**
 * @author ZhangXuanChen
 * @date 2016-11-17
 * @package com.xc.framework.utils
 * @description Viewx相关工具类
 */
public class XCViewUtil {
	/**
	 * 测量宽高
	 * 
	 * @param measureSpec 待测量宽高
	 * @return 测量后宽高
	 */
	public static int getMeasureSize(int measureSpec) {
		int result = 100;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		//
		if (specMode == MeasureSpec.EXACTLY) { // fill_parent
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) { // wrap_content
			result = Math.min(result, specSize);
		}
		return result;
	}
}
