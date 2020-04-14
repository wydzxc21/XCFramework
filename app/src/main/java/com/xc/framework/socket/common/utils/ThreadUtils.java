package com.xc.framework.socket.common.utils;
/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public class ThreadUtils {

    public static void sleep(long mills) {
        long weakTime = 0;
        long startTime = 0;
        while (true) {
            try {
                if (weakTime - startTime < mills) {
                    mills = mills - (weakTime - startTime);
                } else {
                    break;
                }
                startTime = System.currentTimeMillis();
                Thread.sleep(mills);
                weakTime = System.currentTimeMillis();
            } catch (InterruptedException e) {
                weakTime = System.currentTimeMillis();
            }
        }
    }
}
