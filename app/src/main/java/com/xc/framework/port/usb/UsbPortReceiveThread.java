package com.xc.framework.port.usb;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.port.core.FrameHeadUtil;
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
    private byte[] interruptFrameHeads;//中断帧头
    private UsbPort usbPort;
    //
    private boolean isStop;
    private boolean isReceiveFrameHeads;//是否为接收帧头
    private byte[] bufferDatas;//缓存数据
    private int bufferPosition;//缓存索引
    private byte[] receiveDatas;//接收数据

    /**
     * @param usbPortParam usb参数
     * @param usbPort      usb工具
     * @author ZhangXuanChen
     * @date 2020/3/15
     */
    public UsbPortReceiveThread(UsbPortParam usbPortParam, UsbPort usbPort) {
        this.receiveFrameHeads = usbPortParam.getReceiveFrameHeads();
        this.interruptFrameHeads = usbPortParam.getInterruptFrameHeads();
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
        switch (msg.what) {
            case 0x123://接收
                receiveDatas = (byte[]) msg.obj;
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
        byte[] readDatas = usbPort.readUsbPort();
        if (readDatas != null && readDatas.length > 0) {
            System.arraycopy(readDatas, 0, bufferDatas, bufferPosition, readDatas.length);
            bufferPosition += readDatas.length;
            byte[] cutDatas = Arrays.copyOf(bufferDatas, bufferPosition);
            isReceiveFrameHeads = true;
            if (receiveFrameHeads != null && receiveFrameHeads.length > 0 || interruptFrameHeads != null && interruptFrameHeads.length > 0) {//设置了帧头
                int lastFrameHeadPosition = FrameHeadUtil.getLastFrameHeadPosition(receiveFrameHeads, cutDatas);//获取最后一组接收帧头索引
                if (lastFrameHeadPosition < 0) {
                    isReceiveFrameHeads = false;
                    lastFrameHeadPosition = FrameHeadUtil.getLastFrameHeadPosition(interruptFrameHeads, cutDatas);//获取最后一组中断帧头索引
                }
                if (lastFrameHeadPosition < 0) {
                    reset();
                } else {
                    cutDatas = FrameHeadUtil.splitDataByLastFrameHead(lastFrameHeadPosition, cutDatas);//根据最后一组帧头索引分割数据
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
            reset();
            byte[] datas = Arrays.copyOf(cutDatas, length);//重发粘包根据长度截取
            if (isReceiveFrameHeads) {
                Log.i(TAG, "指令-接收:[" + XCByteUtil.byteToHexStr(datas, true) + "]");
                sendMessage(0x123, datas);
            } else {
                Log.i(TAG, "指令-中断:[" + XCByteUtil.byteToHexStr(datas, true) + "]");
                sendMessage(0x234, datas);
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 14:51
     * Description：reset
     */
    public void reset() {
        bufferPosition = 0;
        receiveDatas = null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/3 9:23
     * Description：getReceive
     */
    public byte[] getReceive() {
        return receiveDatas;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 15:07
     * Description：setLength
     */
    public abstract int setLength(byte[] receiveOrInterruptDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onInterrupt
     */
    public abstract void onInterrupt(byte[] interruptDatas);
}
