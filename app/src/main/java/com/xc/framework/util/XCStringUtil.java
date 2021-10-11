package com.xc.framework.util;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
        if (!XCStringUtil.isEmpty(intStr)) {
            Pattern pattern = Pattern.compile("^-?\\d+$");
            isInt = pattern.matcher(intStr).matches();
        }
        return isInt;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description 是否为浮点数
     */
    public static boolean isFloat(String intStr) {
        boolean isFloat = false;
        if (!XCStringUtil.isEmpty(intStr)) {
            Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
            isFloat = pattern.matcher(intStr).matches();
        }
        return isFloat;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/11 16:10
     * Description：toStr
     */
    public static String toStr(String str) {
        return !XCStringUtil.isEmpty(str) ? str : "";
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/7 13:34
     * Description：转int
     */
    public static int toInt(String intStr) {
        int number = 0;
        if (!XCStringUtil.isEmpty(intStr)) {
            number = (int) Float.parseFloat(intStr);
        }
        return number;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/7 13:34
     * Description：转float
     */
    public static float toFloat(String floatStr) {
        float number = 0;
        if (!XCStringUtil.isEmpty(floatStr)) {
            number = Float.parseFloat(floatStr);
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
     * Author：ZhangXuanChen
     * Time：2020/10/15 11:19
     * Description：16进制字符串转字节数组
     */
    public static byte[] toBytes(String hexStr) {
        if (XCStringUtil.isEmpty(hexStr)) {
            return null;
        }
        if (hexStr.contains(" ")) {
            hexStr = hexStr.replaceAll(" ", "").toUpperCase();
        }
        String str = "0123456789ABCDEF";
        char[] hexChar = hexStr.toCharArray();
        int length = hexStr.length() / 2;
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (str.indexOf(hexChar[pos]) << 4 | str.indexOf(hexChar[pos + 1]));
        }
        return d;
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

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/9 9:49
     * Description：转Base64
     */
    public static String toBase64(String decStr) {
        if (XCStringUtil.isEmpty(decStr)) {
            return "";
        }
        return new String(Base64.encodeBase64(decStr.getBytes()));
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
                } else if (split.equals("(")) {
                    return str.split("\\(");
                } else if (split.equals(")")) {
                    return str.split("\\)");
                } else {
                    return str.split(split);
                }
            }
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/10 12:10
     * Description：压缩
     */
    public static String compress(String str) {
        if (XCStringUtil.isEmpty(str)) {
            return "";
        }
        ByteArrayOutputStream out = null;
        GZIPOutputStream gzip = null;
        String compressStr = "";
        try {
            out = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.flush();
            gzip.finish();
            gzip.close();
            compressStr = out.toString("ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (gzip != null) {
                    gzip.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return compressStr;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/10 12:12
     * Description：解压
     */
    public static String uncompress(String compressStr) {
        if (XCStringUtil.isEmpty(compressStr)) {
            return "";
        }
        ByteArrayOutputStream out = null;
        GZIPInputStream gzip = null;
        String uncompressStr = "";
        try {
            out = new ByteArrayOutputStream();
            gzip = new GZIPInputStream(new ByteArrayInputStream(compressStr.getBytes("ISO-8859-1")));
            byte[] buffer = new byte[256];
            int n;
            while ((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            uncompressStr = out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (gzip != null) {
                    gzip.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uncompressStr;
    }

    /**
     * @author wuchengcheng
     * @time 2021/2/4 14:39
     * @Description: 半角转全角 防止UDI标签自动换行
     */
    public static String isToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * @param str   需要被获取的字符串
     * @param regex 正则表达式
     * @return 所有满足正则表达式的字符串
     * @author ChenGuiLin
     * @date 2021/9/24
     * @decription * 获取所有满足正则表达式的字符串
     */
    public static ArrayList<String> getAllSatisfyStr(String str, String regex) {
        ArrayList<String> allSatisfyStr = new ArrayList<>();
        if (isEmpty(str)) {
            return allSatisfyStr;
        }
        if (isEmpty(regex)) {
            allSatisfyStr.add(str);
            return allSatisfyStr;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            allSatisfyStr.add(matcher.group());
        }
        return allSatisfyStr;
    }
}
