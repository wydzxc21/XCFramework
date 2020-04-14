package com.xc.framework.socket.core.utils;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public class BytesUtils {

    /**
     * 生成打印16进制日志所需的字符串
     *
     * @param data 数据源
     * @return 字符串给日志使用
     */
    public static String toHexStringForLog(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                String tempHexStr = Integer.toHexString(data[i] & 0xff) + " ";
                tempHexStr = tempHexStr.length() == 2 ? "0" + tempHexStr : tempHexStr;
                sb.append(tempHexStr);
            }
        }
        return sb.toString();
    }
}
