package com.xc.framework.util;

import android.database.Cursor;

import java.io.Closeable;

/**
 * @author ZhangXuanChen
 * @date 2015-10-14
 * @package com.xc.framework.utils
 * @description IO工具类
 */
public class XCIOUtil {
	
	/**
	 * 关闭
	 * @param closeable closeable对象
	 */
	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable e) {
			}
		}
	}
	/**
	 * 关闭
	 * @param cursor cursor对象
	 */
	public static void close(Cursor cursor) {
		if (cursor != null) {
			try {
				cursor.close();
			} catch (Throwable e) {
			}
		}
	}
}
