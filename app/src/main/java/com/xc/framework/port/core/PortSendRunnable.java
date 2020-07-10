package com.xc.framework.port.core;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCRunnable;
import com.xc.framework.util.XCByteUtil;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：串口发送基类
 */
public abstract class PortSendRunnable extends XCRunnable {
    private final String TAG = "PortSendRunnable";
    private byte[] sendDatas;//发送数据
    private int what;
    private boolean isWaitResponse;//是否等待响应
    private int resendCount;//重发次数
    private int sendTimeout;//发送超时(毫秒)
    private PortReceiveThread portReceiveThread;//接收线程
    //
    boolean isResponse;//是否响应
    int sendCount;//发送次数

    /**
     * @param sendDatas         发送数据
     * @param what              区分消息
     * @param isWaitResponse    是否等待响应
     * @param portParam         串口参数
     * @param portReceiveThread 接收线程
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public PortSendRunnable(byte[] sendDatas, int what, boolean isWaitResponse, PortParam portParam, PortReceiveThread portReceiveThread) {
        this.sendDatas = sendDatas;
        this.what = what;
        this.isWaitResponse = isWaitResponse;
        this.resendCount = portParam.getResendCount();
        this.sendTimeout = portParam.getSendTimeout();
        this.portReceiveThread = portReceiveThread;
    }

    @Override
    protected Object onRun(Handler handler) {
        try {
            writeDatas();
            if (!isResponse && isWaitResponse) {//超时
                if ((sendCount - 1) <= resendCount) {
                    writeDatas();
                } else {
                    sendMessage(0x123);
                }
            }
        } catch (Exception e) {
            isResponse = true;
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        switch (msg.what) {
            case 0x123://超时
                onTimeout(what, sendDatas);
                break;
            case 0x234://响应
                onResponse(what, (byte[]) msg.obj);
                break;
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description writeDatas
     */
    private void writeDatas() throws InterruptedException {
        portReceiveThread.reset();
        if (writePort(sendDatas)) {
            sendCount++;
            Log.i(TAG, "指令-发送:[" + XCByteUtil.byteToHexStr(sendDatas, true) + "],第" + sendCount + "次");
            if (isWaitResponse) {//是否等待接收
                waitResponse();
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/9 13:05
     * Description：waitReceive
     */
    private void waitResponse() throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        do {
            byte[] responseDatas = portReceiveThread.getResponseDatas();
            if (responseDatas != null && responseDatas.length > 0) {
                isResponse = true;
                sendMessage(0x234, responseDatas);
            }
            Thread.sleep(1);
        } while (!isResponse && System.currentTimeMillis() - currentTime < sendTimeout);
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
     * Time：2020/7/10 16:39
     * Description：writePort
     */
    protected abstract boolean writePort(byte[] sendDatas);

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
