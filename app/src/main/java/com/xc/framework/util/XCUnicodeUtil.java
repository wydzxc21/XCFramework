package com.xc.framework.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date：2020/12/11
 * Author：ZhangXuanChen
 * Description：
 */
public class XCUnicodeUtil {

    /**
     * Author：ZhangXuanChen
     * Time：2020/12/11 17:54
     * Description：toStr
     */
    public static String toStr(String ascii) {
        List<String> ascii_s = new ArrayList<String>();
        String zhengz = "\\\\u[0-9,a-f,A-F]{4}";
        Pattern p = Pattern.compile(zhengz);
        Matcher m = p.matcher(ascii);
        while (m.find()) {
            ascii_s.add(m.group());
        }
        for (int i = 0, j = 2; i < ascii_s.size(); i++) {
            String code = ascii_s.get(i).substring(j, j + 4);
            char ch = (char) Integer.parseInt(code, 16);
            ascii = ascii.replace(ascii_s.get(i), String.valueOf(ch));
        }
        return ascii;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/12/11 17:34
     * Description：
     */
    public static String toUnicode(String str) {
        if (XCStringUtil.isEmpty(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (isChinese(c[i])) {
                sb.append("\\u" + Integer.toHexString(c[i]));
            } else {
                sb.append(c[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/12/11 17:36
     * Description：isContainChinese
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
}
