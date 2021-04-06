package com.xc.framework.port.core;


import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCCallable;
import com.xc.framework.util.XCByteUtil;
import com.xc.framework.util.XCThreadUtil;

import java.util.List;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：串口发送基类
 */
public abstract class PortSendCallable extends XCCallable<byte[]> {
    private final String TAG = "PortSendRunnable";
    private long sleepTime;//沉睡时间
    private IPort iPort;//串口工具
    private PortParam portParam;//串口参数
    private byte[] sendDatas;//发送数据
    private PortReceiveType portReceiveType;//接收类型
    private int what;//区分消息
    private PortFilterCallback portFilterCallback;//过滤回调
    //
    private byte[] receiveDatas;//接收数据
    private int sendCount;//发送次数

    /**
     * @param sleepTime          沉睡时间
     * @param iPort              串口工具
     * @param portParam          串口参数
     * @param sendDatas          发送数据
     * @param portReceiveType    接收类型
     * @param what               区分消息
     * @param portFilterCallback 过滤回调
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public PortSendCallable(long sleepTime, IPort iPort, PortParam portParam, byte[] sendDatas, PortReceiveType portReceiveType, int what, PortFilterCallback portFilterCallback) {
        this.sleepTime = sleepTime;
        this.iPort = iPort;
        this.portParam = portParam;
        this.sendDatas = sendDatas;
        this.portReceiveType = portReceiveType;
        this.what = what;
        this.portFilterCallback = portFilterCallback;
    }

    @Override
    public byte[] call() {
        writeDatas();
        if (receiveDatas != null && receiveDatas.length > 0) {
            if (portReceiveType == PortReceiveType.Response) {//响应
                onResponse(what, receiveDatas);
            } else if (portReceiveType == PortReceiveType.Interrupt) {//中断
                onInterrupt(what, receiveDatas);
            } else if (portReceiveType == PortReceiveType.NULL) {
            }
        } else {//超时
            onTimeout(what, sendDatas);
        }
        return receiveDatas;
    }

    @Override
    protected void onHandler(Message msg) {

    }


    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description writeDatas
     */
    private void writeDatas() {
        XCThreadUtil.sleep(sleepTime);
        sendCount++;
        if (sendCount <= portParam.getResendCount()) {
            Log.i(TAG, "指令-发送请求:[" + XCByteUtil.toHexStr(sendDatas, true) + "],第" + sendCount + "次");
            onSend(what, sendDatas, sendCount);
            iPort.writePort(sendDatas);
            if (portReceiveType == PortReceiveType.Response || portReceiveType == PortReceiveType.Interrupt) {//等待响应or中断
                receiveDatas = waitReceive(PortReceiveType.Response);//先等响应
                if (receiveDatas != null && receiveDatas.length > 0) {
                    if (portReceiveType == PortReceiveType.Interrupt) {//中断请求
                        receiveDatas = waitReceive(PortReceiveType.Interrupt);
                    }
                } else if (!isClearSend()) {//重发
                    writeDatas();
                }
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/9 13:05
     * Description：waitReceive
     */
    private byte[] waitReceive(PortReceiveType receiveType) {
        long currentTime = System.currentTimeMillis();
        byte[] receiveDatas = null;
        while (receiveDatas == null && !isClearSend() && isTimeout(currentTime, receiveType)) {
            XCThreadUtil.sleep(1);
            if (isPauseReceive()) {
                currentTime = System.currentTimeMillis();
                continue;
            }
            List<byte[]> receiveList = null;
            if (receiveType == PortReceiveType.Response) {//响应
                receiveList = PortReceiveCache.getInstance().getResponseList();
            } else if (receiveType == PortReceiveType.Interrupt) {//中断
                receiveList = PortReceiveCache.getInstance().getInterruptList();
            }
            receiveDatas = PortReceiveCache.getInstance().getReceiveDatas(receiveList, sendDatas, portFilterCallback);
        }
        return receiveDatas;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 12:45
     * Description：isTimeout
     */
    private boolean isTimeout(long currentTime, PortReceiveType receiveType) {
        long runtime = System.currentTimeMillis() - currentTime;
        long timeout = receiveType == PortReceiveType.Response ? portParam.getSendTimeout() : portParam.getInterruptTimeout();
        return runtime < timeout;
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
     * Description：onSend
     */
    public abstract void onSend(int what, byte[] sendDatas, int sendCount);

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 11:18
     * Description：isClearSend
     */
    public abstract boolean isClearSend();

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/10 15:25
     * Description：isPauseReceive
     */
    public abstract boolean isPauseReceive();

}
