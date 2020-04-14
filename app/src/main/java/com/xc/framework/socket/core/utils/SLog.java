package com.xc.framework.socket.core.utils;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public class SLog {

    private static boolean isDebug;

    public static void setIsDebug(boolean isDebug) {
        SLog.isDebug = isDebug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void e(String msg) {
        if (isDebug) {
            System.err.println("OkSocket, " + msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            System.out.println("OkSocket, " + msg);
        }
    }

    public static void w(String msg) {
        i(msg);
    }
}
