package com.xc.framework.port.serial;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCRunnable;
import com.xc.framework.util.XCByteUtil;
import com.xc.framework.util.XCThreadUtil;

import java.util.Arrays;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：串口任务
 */
public class SerialPortRunnable extends XCRunnable {
    private final String TAG = "SerialPortRunnable";
    byte[] sendDatas;//发送数据
    int what;
    int retryCount;//重发次数
    int timeout;//超时(毫秒)
    byte[] frameHeaders;//帧头
    SerialPort serialPort;
    OnSerialPortListener onSerialPortListener;
    //
    byte[] bufferDatas;//缓存数据
    int bufferPosition;//缓存索引
    long writeTime;//写入时间
    int writeCount;//写入次数

    /**
     * @param sendDatas    发送数据
     * @param what         区分数据
     * @param retryCount   重发次数
     * @param timeout      超时(毫秒)
     * @param frameHeaders 帧头
     * @param serialPort   串口工具
     * @return
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description
     */
    public SerialPortRunnable(byte[] sendDatas, int what, int retryCount, int timeout, byte[] frameHeaders, SerialPort serialPort, OnSerialPortListener onSerialPortListener) {
        this.sendDatas = sendDatas;
        this.what = what;
        this.retryCount = retryCount > 0 ? retryCount : 0;
        this.timeout = timeout > 0 ? timeout : 2000;
        this.frameHeaders = frameHeaders;
        this.serialPort = serialPort;
        this.onSerialPortListener = onSerialPortListener;
        this.bufferDatas = new byte[16 * 1024];
    }

    @Override
    protected Object onRun(Handler handler) {
        writeDatas(handler);
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        switch (msg.what) {
            case 0x123:
                if (onSerialPortListener != null) {
                    onSerialPortListener.onReceive(what, (byte[]) msg.obj);
                }
                break;
            case 0x234:
                if (onSerialPortListener != null) {
                    onSerialPortListener.onTimeout(what, sendDatas);
                }
                break;
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description writeDatas
     */
    private void writeDatas(Handler handler) {
        if (serialPort.writeSerialPort(sendDatas)) {
            writeTime = System.currentTimeMillis();
            writeCount++;
            Log.i(TAG, "指令-发送:[ " + XCByteUtil.byteToHexStr(sendDatas) + "],第" + writeCount + "次");
            readDatas(handler);
        }
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description readDatas
     */
    private void readDatas(Handler handler) {
        byte[] readDatas = null;
        while (readDatas == null && System.currentTimeMillis() - writeTime < timeout) {
            readDatas = serialPort.readSerialPort();
            XCThreadUtil.sleep(1);
        }
        if (readDatas != null && readDatas.length > 0) {
            System.arraycopy(readDatas, 0, bufferDatas, bufferPosition, readDatas.length);
            bufferPosition += readDatas.length;
            byte[] cutDatas = Arrays.copyOf(bufferDatas, bufferPosition);
            if (frameHeaders != null && frameHeaders.length > 0) { //根据最后一组帧头分割数据
                cutDatas = splitDataByLastFrameHeader(frameHeaders, cutDatas);
            }
            int length = cutDatas.length;
            if (onSerialPortListener != null) {
                length = onSerialPortListener.setLength(what, cutDatas);//判断指令长度
            }
            if (length > 0 && length <= cutDatas.length) {
                byte[] receiveDatas = Arrays.copyOf(cutDatas, length);//重发粘包根据长度截取
                Log.i(TAG, "指令-接收:[ " + XCByteUtil.byteToHexStr(receiveDatas) + "]");
                sendMessage(0x123, receiveDatas);
            } else {//长度不足继续读取
                readDatas(handler);
            }
        } else if ((writeCount - 1) < retryCount) {//超时优先重发
            writeDatas(handler);
        } else {
            sendMessage(0x234);
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 根据最后一组帧头索引分割数据
     */
    private byte[] splitDataByLastFrameHeader(byte[] frameHeaders, byte[] cutDatas) {
        if (frameHeaders == null || frameHeaders.length <= 0 || cutDatas == null || cutDatas.length <= 0) {
            return null;
        }
        if (frameHeaders.length > cutDatas.length) {
            return null;
        }
        int frameHeaderPosition = getLastFrameHeaderPosition(frameHeaders, cutDatas);
        byte[] splitData = new byte[cutDatas.length - frameHeaderPosition];
        System.arraycopy(cutDatas, frameHeaderPosition, splitData, 0, splitData.length);
        return splitData;
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 获取最后一组帧头索引
     */
    private int getLastFrameHeaderPosition(byte[] frameHeaders, byte[] cutDatas) {
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

}
