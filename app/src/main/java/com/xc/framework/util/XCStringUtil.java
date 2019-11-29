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
    public static String hexToString(String str) {
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

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/28 15:06
     * Description：十六进制转字节数组
     */
    public static byte[] hexStringToBytes(String src) {
        src = src.replace(" ", "");
        byte[] res = new byte[src.length() / 2];
        char[] chs = src.toCharArray();
        for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
            res[c] = (byte) (Integer.parseInt(new String(chs, i, 2), 16));
        }

        return res;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/28 14:58
     * Description：字节数组转字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }


}
