package com.xc.framework.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * @author ZhangXuanChen
 * @date 2015-11-23
 * @package com.easttime.beauty.utils
 * @description MD5工具类
 */
public class XCMD5Util {
	/** 大写标记 */
	public static final int CASE_UPPER = 1;
	/** 小写标记 */
	public static final int CASE_LOWER = 2;

	/**
	 * 获取字符串MD5值
	 * @param str 需要加密字符串
	 * @return 小写加密结果值
	 */
	public static String getMD5(String str) {
		return getMD5(str, CASE_LOWER);
	}
	/**
	 * 获取字符串MD5值
	 * @param str 需要加密字符串
	 * @param caseFlag 大小写
	 * @return 结果值
	 */
	public static String getMD5(String str, int caseFlag) {
		String md5Str = "";
		if (!XCStringUtil.isEmpty(str)) {
			// 16 进制字符数组
			char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
				md.update(str.getBytes());
				// MD5 的计算结果是128 位的长整数，用字节表示就是16 个字节
				byte[] tmp = md.digest();
				char[] charArray = new char[16 * 2];
				// 表示转换结果中对应的字符位置
				int pos = 0;
				for (int i = 0; i < 16; i++) {
					byte ch = tmp[i];
					charArray[pos++] = hexDigits[ch >>> 4 & 0xf];
					charArray[pos++] = hexDigits[ch & 0xf];
				}
				Locale locale = Locale.getDefault();
				if (caseFlag == CASE_UPPER) {
					md5Str = new String(charArray).toUpperCase(locale);
				} else {
					md5Str = new String(charArray).toLowerCase(locale);
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return md5Str;
	}
}
