package com.xc.framework.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

/**
 * @author ZhangXuanChen
 * @date 2015-10-14
 * @package com.xc.framework.utils
 * @description 文件工具类
 */
public class XCFileUtil {
	/**
	 * SDCard是否存在
	 * 
	 * @return 是否存在
	 */
	public static boolean isSDCardExist() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return true;
		}
		return false;
	}

	/**
	 * 获取SDCard根目录
	 * 
	 * @return 例:/storage/emulated/0
	 */
	public static String getSDCardRootDir() {
		if (isSDCardExist()) {
			String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			if (!XCStringUtil.isEmpty(rootPath)) {
				return rootPath;
			}
		}
		return "";
	}

	/**
	 * 获取手机缓存路径
	 * 
	 * @param context
	 *            上下文
	 * @return 不可管理,卸载不会被删.例:
	 *         /storage/emulated/0/Android/data/com.xc.sample/cache 或
	 *         可管理,卸载会被删.例: /data/data/com.xc.sample/cache
	 */
	public static String getDiskCacheDir(Context context) {
		String cachePath = "";
		if (isSDCardExist()) {
			// 不可管理,卸载不会被删.例:
			// /storage/emulated/0/Android/data/com.xc.sample/cache
			File externalCacheDir = context.getExternalCacheDir();
			if (externalCacheDir != null) {
				cachePath = externalCacheDir.getPath();
			}
		}
		// 可管理,卸载会被删.例: /data/data/com.xc.sample/cache
		if ("".equals(cachePath)) {
			File cacheDir = context.getCacheDir();
			if (cacheDir != null && cacheDir.exists()) {
				cachePath = cacheDir.getPath();
			}
		}
		return cachePath;
	}

	/**
	 * 获取下载文件名
	 * 
	 * @param downloadUrl
	 *            下载地址
	 * @return 文件名
	 */
	public static String getDownloadFileName(String downloadUrl) {
		if (!XCStringUtil.isEmpty(downloadUrl)) {
			try {
				if (downloadUrl.contains("/")) {
					String[] split = downloadUrl.split("/");
					String temp = split[split.length - 1];
					if (temp.contains("?")) {
						return temp.split("\\?")[0];
					} else {
						return temp;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * 获取一个已时间命名的文件名
	 * 
	 * @param suffix
	 *            文件后缀名
	 * @return 文件名
	 */
	public static String getFileName(String suffix) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'xc'_yyyyMMddHHmmss", Locale.CHINESE);
		String randomNumber = "_" + new Random().nextInt(10000);
		return dateFormat.format(date) + randomNumber + suffix;
	}
}
