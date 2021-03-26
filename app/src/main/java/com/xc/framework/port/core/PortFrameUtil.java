package com.xc.framework.port.core;

/**
 * Date：2020/5/19
 * Author：ZhangXuanChen
 * Description：串口帧工具
 */
public class PortFrameUtil {

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 根据最前一组帧头索引分割数据
     */
    public static byte[][] splitDataByFirstFrameHead(int firstFrameHeadPosition, int length, byte[] cutDatas) {
        if (firstFrameHeadPosition < 0 || cutDatas == null || cutDatas.length <= 0) {
            return null;
        }
        //一条完整数据
        byte[] splitOne = new byte[length > 0 ? length : cutDatas.length - firstFrameHeadPosition];
        System.arraycopy(cutDatas, firstFrameHeadPosition, splitOne, 0, splitOne.length);
        //剩余数据
        byte[] splitTwo = new byte[cutDatas.length - firstFrameHeadPosition - splitOne.length];
        System.arraycopy(cutDatas, splitOne.length, splitTwo, 0, splitTwo.length);
        return new byte[][]{splitOne, splitTwo};
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 获取最前一组帧头索引
     */
    public static int getFirstFrameHeadPosition(byte[] frameHeaders, byte[] cutDatas) {
        if (frameHeaders == null || frameHeaders.length <= 0 || cutDatas == null || cutDatas.length <= 0) {
            return -1;
        }
        if (frameHeaders.length > cutDatas.length) {
            return -1;
        }
        int headerPosition = -1;
        for (int i = 0; i < cutDatas.length; i++) {
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
