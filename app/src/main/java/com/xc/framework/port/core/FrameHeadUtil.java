package com.xc.framework.port.core;

/**
 * Date：2020/5/19
 * Author：ZhangXuanChen
 * Description：帧头工具
 */
public class FrameHeadUtil {
    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 根据最后一组帧头索引分割数据
     */
    public static byte[] splitDataByLastFrameHead(int lastFrameHeadPosition, byte[] cutDatas) {
        if (lastFrameHeadPosition < 0 || cutDatas == null || cutDatas.length <= 0) {
            return null;
        }
        byte[] splitData = new byte[cutDatas.length - lastFrameHeadPosition];
        System.arraycopy(cutDatas, lastFrameHeadPosition, splitData, 0, splitData.length);
        return splitData;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 获取最后一组帧头索引
     */
    public static  int getLastFrameHeadPosition(byte[] frameHeaders, byte[] cutDatas) {
        if (frameHeaders == null || frameHeaders.length <= 0 || cutDatas == null || cutDatas.length <= 0) {
            return -1;
        }
        if (frameHeaders.length > cutDatas.length) {
            return -1;
        }
        int headerPosition = -1;
        for (int i = cutDatas.length - 1; i >= 0; i--) {
            if (cutDatas[i] == frameHeaders[0]) {
                headerPosition = i;//第一位帧头索引
                for (int k = 0; k < frameHeaders.length; k++) {
                    int l = k + i;//从第一位帧头索引按顺序匹配帧头数组
                    if (l >= cutDatas.length || frameHeaders[k] != cutDatas[l]) {
                        headerPosition = -1;
                        break;
                    }
                }
                if (headerPosition >= 0) {
                    break;
                }
            }
        }
        return headerPosition;
    }
}
