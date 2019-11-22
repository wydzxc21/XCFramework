package com.xc.framework.util;

/**
 * @author ZhangXuanChen
 * @date 2015-9-18
 * @package com.xc.framework.utils
 * @description 字符串工具类
 */
public class XCStringUtil {
	/**
	 * 判断字符串是否为空
	 * 
	 * @param str 字符串
	 * @return 是否为:非null、非""、非"null"
	 */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str) && !"null".equals(str)) {
			return false;
		}
		return true;
	}

	/**
	 * 十六进制转字符串
	 * 
	 * @param str 16进制字符串
	 * @return 结果值
	 */
	public static String toStringHex(String str) {
		if (!XCStringUtil.isEmpty(str)) {
			byte[] baKeyword = new byte[str.length() / 2];
			for (int i = 0; i < baKeyword.length; i++) {
				try {
					baKeyword[i] = (byte) (0xff & Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				str = new String(baKeyword, "utf-8");// UTF-16le:Not
			} catch (Exception e1) {

			}
		}
		return str;
	}
}
