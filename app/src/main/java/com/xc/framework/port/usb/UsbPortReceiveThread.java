package com.xc.framework.port.usb;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCByteUtil;

import java.util.Arrays;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：usb接收
 */
public abstract class UsbPortReceiveThread extends XCThread {
    private final String TAG = "UsbPortReceiveThread";
    private byte[] receiveFrameHeads;//接收帧头
    private UsbPort usbPort;
    //
    boolean isStop;
    private byte[] bufferDatas;//缓存数据
    private int bufferPosition;//缓存索引

    /**
     * @param usbPortParam usb参数
     * @param usbPort      usb工具
     * @author ZhangXuanChen
     * @date 2020/3/15
     */
    public UsbPortReceiveThread(UsbPortParam usbPortParam, UsbPort usbPort) {
        this.receiveFrameHeads = usbPortParam.getReceiveFrameHeads();
        this.usbPort = usbPort;
        this.bufferDatas = new byte[16 * 1024];
        this.bufferPosition = 0;
    }


    @Override
    protected Object onRun(Handler handler) {
        try {
            while (!isStop) {
                readDatas();
                Thread.sleep(1);
            }
        } catch (Exception e) {
            isStop = true;
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        onReceive((byte[]) msg.obj);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description readDatas
     */
    private void readDatas() {
        byte[] readDatas = usbPort.readUsbPort();
        if (readDatas != null && readDatas.length > 0) {
            System.arraycopy(readDatas, 0, bufferDatas, bufferPosition, readDatas.length);
            bufferPosition += readDatas.length;
            byte[] cutDatas = Arrays.copyOf(bufferDatas, bufferPosition);
            if (receiveFrameHeads != null && receiveFrameHeads.length > 0) { //根据最后一组帧头分割数据
                cutDatas = splitDataByLastFrameHead(receiveFrameHeads, cutDatas);
            }
            int length = setLength(cutDatas);//判断指令长度
            if (length > 0 && length <= cutDatas.length) {
                bufferPosition = 0;
                byte[] receiveDatas = Arrays.copyOf(cutDatas, length);//重发粘包根据长度截取
                Log.i(TAG, "指令-接收:[" + XCByteUtil.byteToHexStr(receiveDatas) + "]");
                sendMessage(0x123, receiveDatas);
            } else {//长度不足继续读取
                readDatas();
            }
        }
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 根据最后一组帧头索引分割数据
     */
    private byte[] splitDataByLastFrameHead(byte[] frameHeaders, byte[] cutDatas) {
        if (frameHeaders == null || frameHeaders.length <= 0 || cutDatas == null || cutDatas.length <= 0) {
            return null;
        }
        if (frameHeaders.length > cutDatas.length) {
            return null;
        }
        int frameHeaderPosition = getLastFrameHeadPosition(frameHeaders, cutDatas);
        byte[] splitData = new byte[cutDatas.length - frameHeaderPosition];
        System.arraycopy(cutDatas, frameHeaderPosition, splitData, 0, splitData.length);
        return splitData;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 获取最后一组帧头索引
     */
    private int getLastFrameHeadPosition(byte[] frameHeaders, byte[] cutDatas) {
        if (frameHeaders == null || frameHeaders.length <= 0 || cutDatas == null || cutDatas.length <= 0) {
            return 0;
        }
        if (frameHeaders.length > cutDatas.length) {
            return 0;
        }
        int headerPosition = 0;
        for (int i = cutDatas.length - 1; i >= 0; i--) {
            if (cutDatas[i] == frameHeaders[0]) {
                headerPosition = i;//第一位帧头索引
                for (int k = 0; k < frameHeaders.length; k++) {
                    int l = k + i;//从第一位帧头索引按顺序匹配帧头数组
                    if (l >= cutDatas.length || frameHeaders[k] != cutDatas[l]) {
                        headerPosition = 0;
                        break;
                    }
                }
                if (headerPosition > 0) {
                    break;
                }
            }
        }
        return headerPosition;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 15:07
     * Description：
     */
    public abstract int setLength(byte[] receiveDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onReceive
     */
    public abstract void onReceive(byte[] receiveDatas);
}
