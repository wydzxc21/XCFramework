package com.xc.framework.port.serial;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCByteUtil;

import java.util.Arrays;

/**
 * Date：2020/3/10
 * Author：ZhangXuanChen
 * Description：串口接收
 */
public abstract class SerialPortReceiveThread extends XCThread {
    private final String TAG = "SerialPortReceiveThread";
    private byte[] receiveFrameHeads;//接收帧头
    private byte[] interruptFrameHeads;//中断帧头
    private SerialPort serialPort;
    //
    private boolean isStop;
    private boolean isReceive;//是否为接收数据
    private byte[] bufferDatas;//缓存数据
    private int bufferPosition;//缓存索引

    /**
     * @param serialPortParam 串口参数
     * @param serialPort      串口工具
     * @author ZhangXuanChen
     * @date 2020/3/15
     */
    public SerialPortReceiveThread(SerialPortParam serialPortParam, SerialPort serialPort) {
        this.receiveFrameHeads = serialPortParam.getReceiveFrameHeads();
        this.interruptFrameHeads = serialPortParam.getInterruptFrameHeads();
        this.serialPort = serialPort;
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
        switch (msg.what) {
            case 0x123://接收
                onReceive((byte[]) msg.obj);
                break;
            case 0x234://中断
                onInterrupt((byte[]) msg.obj);
                break;
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description readDatas
     */
    private void readDatas() {
        byte[] readDatas = serialPort.readSerialPort();
        if (readDatas != null && readDatas.length > 0) {
            System.arraycopy(readDatas, 0, bufferDatas, bufferPosition, readDatas.length);
            bufferPosition += readDatas.length;
            byte[] cutDatas = Arrays.copyOf(bufferDatas, bufferPosition);
            isReceive = true;
            if (receiveFrameHeads != null && receiveFrameHeads.length > 0 || interruptFrameHeads != null && interruptFrameHeads.length > 0) {//设置了帧头
                int lastFrameHeadPosition = getLastFrameHeadPosition(receiveFrameHeads, cutDatas);//获取最后一组接收帧头索引
                if (lastFrameHeadPosition < 0) {
                    isReceive = false;
                    lastFrameHeadPosition = getLastFrameHeadPosition(interruptFrameHeads, cutDatas);//获取最后一组中断帧头索引
                }
                if (lastFrameHeadPosition < 0) {
                    receive();
                } else {
                    cutDatas = splitDataByLastFrameHead(lastFrameHeadPosition, cutDatas);//根据最后一组帧头索引分割数据
                    result(cutDatas);
                }
            } else {//未设置帧头
                result(cutDatas);
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/5/18 17:38
     * Description：结果
     */
    private void result(byte[] cutDatas) {
        if (cutDatas == null || cutDatas.length <= 0) {
            return;
        }
        int length = setLength(cutDatas);//判断指令长度
        if (length > 0 && length <= cutDatas.length) {
            receive();
            byte[] datas = Arrays.copyOf(cutDatas, length);//重发粘包根据长度截取
            if (isReceive) {
                Log.i(TAG, "指令-接收:[" + XCByteUtil.byteToHexStr(datas, true) + "]");
                sendMessage(0x123, datas);
            } else {
                Log.i(TAG, "指令-中断:[" + XCByteUtil.byteToHexStr(datas, true) + "]");
                sendMessage(0x234, datas);
            }
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description 根据最后一组帧头索引分割数据
     */
    private byte[] splitDataByLastFrameHead(int lastFrameHeadPosition, byte[] cutDatas) {
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
    private int getLastFrameHeadPosition(byte[] frameHeaders, byte[] cutDatas) {
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

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 14:51
     * Description：receive
     */
    public void receive() {
        bufferPosition = 0;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 15:07
     * Description：
     */
    public abstract int setLength(byte[] receiveOrInterruptDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onReceive
     */
    public abstract void onReceive(byte[] receiveDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onInterrupt
     */
    public abstract void onInterrupt(byte[] interruptDatas);
}
