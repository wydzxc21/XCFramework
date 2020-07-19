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
    private boolean isWaitResponse;//是否等待响应
    private int what;
    private IPort iPort;//串口工具
    private int resendCount;//重发次数
    private int sendTimeout;//发送超时(毫秒)
    private PortReceiveThread portReceiveThread;//接收线程
    private int sendCount;//发送次数

    /**
     * @param sendDatas         发送数据
     * @param isWaitResponse    是否等待响应
     * @param what              区分消息
     * @param portParam         串口参数
     * @param iPort             串口工具
     * @param portReceiveThread 接收线程
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public PortSendCallable(byte[] sendDatas, boolean isWaitResponse, int what, PortParam portParam, IPort iPort, PortReceiveThread portReceiveThread) {
        this.sendDatas = sendDatas;
        this.what = what;
        this.isWaitResponse = isWaitResponse;
        this.resendCount = portParam.getResendCount();
        this.sendTimeout = portParam.getSendTimeout();
        this.iPort = iPort;
        this.portReceiveThread = portReceiveThread;
    }

    @Override
    public byte[] call() throws Exception {
        byte[] responseDatas = null;
        try {
            responseDatas = writeDatas();
            if (responseDatas != null && responseDatas.length > 0) {
                sendMessage(0x123, responseDatas);
            } else {//超时
                sendMessage(0x234);
            }
        } catch (Exception e) {
        }
        return responseDatas;
    }

    @Override
    protected void onHandler(Message msg) {
        switch (msg.what) {
            case 0x123://响应
                onResponse(what, (byte[]) msg.obj);
                break;
            case 0x234://超时
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
        if (sendCount <= resendCount) {
            iPort.writePort(sendDatas);
            Log.i(TAG, "指令-发送:[" + XCByteUtil.toHexStr(sendDatas, true) + "],第" + sendCount + "次");
            if (isWaitResponse) {//是否等待响应
                byte[] responseDatas = waitResponse();
                if (responseDatas != null && responseDatas.length > 0) {
                    return responseDatas;
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
    private byte[] waitResponse() throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        do {
            byte[] responseDatas = portReceiveThread.getResponseDatas();
            if (responseDatas != null && responseDatas.length > 0) {
                return responseDatas;
            }
            Thread.sleep(1);
        } while (System.currentTimeMillis() - currentTime < sendTimeout);
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
     * Time：2020/3/10 11:18
     * Description：onTimeout
     */
    public abstract void onTimeout(int what, byte[] sendDatas);
}
