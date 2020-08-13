package com.xc.framework.port.core;


import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCCallable;
import com.xc.framework.util.XCByteUtil;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：串口发送基类
 */
public abstract class PortSendCallable extends XCCallable<byte[]> {
    private final String TAG = "PortSendRunnable";
    private byte[] sendDatas;//发送数据
    private PortReceiveType portReceiveType;//接收类型
    private int what;
    private PortParam portParam;//串口参数
    private IPort iPort;//串口工具
    private LinkedBlockingQueue<PortSendCallable> linkedBlockingQueue;//正在执行的发送线程
    //
    private int sendCount;//发送次数

    /**
     * @param sendDatas           发送数据
     * @param portReceiveType     接收类型
     * @param what                区分消息
     * @param portParam           串口参数
     * @param iPort               串口工具
     * @param linkedBlockingQueue 正在执行的发送线程
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public PortSendCallable(byte[] sendDatas, PortReceiveType portReceiveType, int what, PortParam portParam, IPort iPort, LinkedBlockingQueue<PortSendCallable> linkedBlockingQueue) {
        this.sendDatas = sendDatas;
        this.what = what;
        this.portReceiveType = portReceiveType;
        this.portParam = portParam;
        this.iPort = iPort;
        this.linkedBlockingQueue = linkedBlockingQueue;
    }

    @Override
    public byte[] call() throws Exception {
        linkedBlockingQueue.put(this);
        byte[] receiveDatas = null;
        try {
            receiveDatas = writeDatas();
            if (receiveDatas != null && receiveDatas.length > 0) {
                if (portReceiveType == PortReceiveType.Response) {//响应
                    sendMessage(0x123, receiveDatas);
                } else if (portReceiveType == PortReceiveType.Interrupt) {//中断
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
        sendCount++;
        if (sendCount <= portParam.getResendCount()) {
            iPort.writePort(sendDatas);
            Log.i(TAG, "指令-发送请求:[" + XCByteUtil.toHexStr(sendDatas, true) + "],第" + sendCount + "次");
            if (portReceiveType == PortReceiveType.Response || portReceiveType == PortReceiveType.Interrupt) {//等待响应or中断
                byte[] receiveDatas = waitReceive(PortReceiveType.Response);//先等响应
                if (receiveDatas != null && receiveDatas.length > 0) {
                    if (portReceiveType == PortReceiveType.Interrupt) {//中断请求
                        receiveDatas = waitReceive(PortReceiveType.Interrupt);
                    }
                    return receiveDatas;
                } else if (!isStopSend()) {//重发
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
    boolean isMatchLog;
    byte[] responseDatas;//响应数据
    byte[] interruptDatas;//中断数据

    private byte[] waitReceive(PortReceiveType receiveType) throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        do {
            byte[] receiveDatas = null;
            if (receiveType == PortReceiveType.Response) {//响应
                receiveDatas = responseDatas;
            } else if (receiveType == PortReceiveType.Interrupt) {//中断
                receiveDatas = interruptDatas;
            }
            //
            if (receiveDatas != null && receiveDatas.length > 0) {
                if (portParam.portParamCallback != null ? portParam.portParamCallback.onMatch(sendDatas, receiveDatas) : true) {//判断指令正确性
                    return receiveDatas;
                } else if (!isMatchLog) {
                    isMatchLog = true;
                    Log.i(TAG, "指令-未能匹配:[" + XCByteUtil.toHexStr(sendDatas, true) + "]" + " , [" + XCByteUtil.toHexStr(receiveDatas, true) + "]");
                }
            }
            Thread.sleep(1);
        }
        while (!isStopSend() && System.currentTimeMillis() - currentTime < (receiveType == PortReceiveType.Response ? portParam.getSendTimeout() : portParam.getInterruptTimeout()));
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/13 14:08
     * Description：setResponseDatas
     */
    public void setResponseDatas(byte[] responseDatas) {
        this.responseDatas = responseDatas;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/13 14:08
     * Description：setInterruptDatas
     */
    public void setInterruptDatas(byte[] interruptDatas) {
        this.interruptDatas = interruptDatas;
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

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 11:18
     * Description：isStopSend
     */
    public abstract boolean isStopSend();

}
