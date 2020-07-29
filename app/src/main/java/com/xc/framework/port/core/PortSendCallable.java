package com.xc.framework.port.core;


import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCCallable;
import com.xc.framework.util.XCByteUtil;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：串口发送基类
 */
public abstract class PortSendCallable extends XCCallable<byte[]> {
    private final String TAG = "PortSendRunnable";
    private byte[] sendDatas;//发送数据
    private ReceiveType receiveType;//接收类型
    private int what;
    private PortParam portParam;//串口参数
    private IPort iPort;//串口工具
    private PortReceiveThread portReceiveThread;//接收线程
    //
    private int sendCount;//发送次数

    /**
     * @param sendDatas         发送数据
     * @param receiveType       接收类型
     * @param what              区分消息
     * @param portParam         串口参数
     * @param iPort             串口工具
     * @param portReceiveThread 接收线程
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public PortSendCallable(byte[] sendDatas, ReceiveType receiveType, int what, PortParam portParam, IPort iPort, PortReceiveThread portReceiveThread) {
        this.sendDatas = sendDatas;
        this.what = what;
        this.receiveType = receiveType;
        this.portParam = portParam;
        this.iPort = iPort;
        this.portReceiveThread = portReceiveThread;
    }

    @Override
    public byte[] call() throws Exception {
        byte[] receiveDatas = null;
        try {
            receiveDatas = writeDatas();
            if (receiveDatas != null && receiveDatas.length > 0) {
                if (receiveType == ReceiveType.Response) {//响应
                    sendMessage(0x123, receiveDatas);
                } else if (receiveType == ReceiveType.Interrupt) {//中断
                    sendMessage(0x234, receiveDatas);
                }
            } else {//超时
                sendMessage(0x345);
            }
        } catch (Exception e) {
        }
        return receiveDatas;
    }

    @Override
    protected void onHandler(Message msg) {
        switch (msg.what) {
            case 0x123://响应
                onResponse(what, (byte[]) msg.obj);
                break;
            case 0x234://中断
                onInterrupt(what, (byte[]) msg.obj);
                break;
            case 0x345://超时
                onTimeout(what, sendDatas);
                break;
        }
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description writeDatas
     */
    private byte[] writeDatas() throws InterruptedException {
        portReceiveThread.reset();
        sendCount++;
        if (sendCount <= portParam.getResendCount()) {
            iPort.writePort(sendDatas);
            Log.i(TAG, "指令-发送请求:[" + XCByteUtil.toHexStr(sendDatas, true) + "],第" + sendCount + "次");
            if (receiveType == ReceiveType.Response || receiveType == ReceiveType.Interrupt) {//等待响应or中断
                byte[] receiveDatas = waitReceive();
                if (receiveDatas != null && receiveDatas.length > 0) {
                    return receiveDatas;
                } else {//重发
                    writeDatas();
                }
            }
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/9 13:05
     * Description：waitReceive
     */
    private byte[] waitReceive() throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        do {
            byte[] receiveDatas = null;
            if (receiveType == ReceiveType.Response) {//响应
                receiveDatas = portReceiveThread.getResponseDatas();
            } else if (receiveType == ReceiveType.Interrupt) {//中断
                receiveDatas = portReceiveThread.getInterruptDatas();
            }
            //
            if (receiveDatas != null && receiveDatas.length > 0) {
                return receiveDatas;
            }
            Thread.sleep(1);
        }
        while (System.currentTimeMillis() - currentTime < (receiveType == ReceiveType.Response ? portParam.getSendTimeout() : portParam.getInterruptTimeout()));
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 15:18
     * Description：what
     */
    public int getWhat() {
        return what;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onResponse
     */
    public abstract void onResponse(int what, byte[] responseDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onInterrupt
     */
    public abstract void onInterrupt(int what, byte[] interruptDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 11:18
     * Description：onTimeout
     */
    public abstract void onTimeout(int what, byte[] sendDatas);
}
