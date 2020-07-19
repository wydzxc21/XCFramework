package com.xc.framework.util;

import java.util.regex.Pattern;

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
        if (str != null && !"".equals(str) && !"null".equalsIgnoreCase(str)) {
            return false;
        }
        return true;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 是否为数字
     */
    public static boolean isInt(String intStr) {
        boolean isInt = false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (!XCStringUtil.isEmpty(intStr)) {
            isInt = pattern.matcher(intStr).matches();
        }
        return isInt;
    }


    /**
     * Author：ZhangXuanChen
     * Time：2020/4/3 8:22
     * Description：获取截取字符串
     */
    public static String[] split(String str, String split) {
        if (!XCStringUtil.isEmpty(str) && !XCStringUtil.isEmpty(split)) {
            if (str.contains(split)) {
                if (split.equals(".")) {
                    return str.split("\\.");
                } else if (split.equals("|")) {
                    return str.split("\\|");
                } else if (split.equals("*")) {
                    return str.split("\\*");
                } else if (split.equals("+")) {
                    return str.split("\\+");
                } else {
                    return str.split(split);
                }
            }
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/7 13:34
     * Description：转int
     */
    public static int toInt(String intStr) {
        int number = 0;
        if (!XCStringUtil.isEmpty(intStr)) {
            if (XCStringUtil.isInt(intStr)) {
                number = Integer.parseInt(intStr);
            }
        }
        return number;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 16进制字符串转10进制字符串
     */
    public static String toDecStr(String hexStr) {
        return toDecStr(hexStr, false);
    }

    /**
     * @param isSpace 是否加空格
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 16进制字符串转10进制字符串
     */
    public static String toDecStr(String hexStr, boolean isSpace) {
        if (XCStringUtil.isEmpty(hexStr)) {
            return "";
        }
        if (hexStr.contains(" ")) {
            hexStr = hexStr.replaceAll(" ", "").toUpperCase();
        }
        if (hexStr.length() == 1) {
            hexStr = "0" + hexStr;
        }
        String str = "0123456789ABCDEF";
        char[] hexChar = hexStr.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hexChar.length / 2; i++) {
            int n = str.indexOf(hexChar[2 * i]) * 16;
            n += str.indexOf(hexChar[2 * i + 1]);
            sb.append(n & 0xff);
            if (isSpace) {
                sb.append(" ");
            }
        }
        if (isSpace) {
            return sb.substring(0, sb.length() - 1);
        } else {
            return sb.toString();
        }
    }
}
