package com.xc.framework.port.serial;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCRunnable;
import com.xc.framework.util.XCByteUtil;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：串口发送
 */
public abstract class SerialPortSendRunnable extends XCRunnable {
    private final String TAG = "SerialPortSendRunnable";
    private byte[] sendDatas;//发送数据
    private int what;
    private int resendCount;//重发次数
    private int sendTimeout;//发送超时(毫秒)
    private SerialPort mSerialPort;
    //
    boolean isReceive;//是否接收
    int sendCount;//发送次数

    /**
     * @param sendDatas            发送数据
     * @param what                 区分消息
     * @param resendCount          重发次数
     * @param sendTimeout          发送超时(毫秒)
     * @param mSerialPort          串口工具
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public SerialPortSendRunnable(byte[] sendDatas, int what, int resendCount, int sendTimeout, SerialPort mSerialPort) {
        init(sendDatas, what, resendCount, sendTimeout, mSerialPort);
    }

    public SerialPortSendRunnable(byte[] sendDatas, int what, SerialPortParam mSerialPortParam, SerialPort mSerialPort) {
        init(sendDatas, what, mSerialPortParam.getResendCount(), mSerialPortParam.getSendTimeout(), mSerialPort);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 10:50
     * Description：init
     */
    private void init(byte[] sendDatas, int what, int resendCount, int sendTimeout, SerialPort mSerialPort) {
        this.sendDatas = sendDatas;
        this.what = what;
        this.resendCount = resendCount;
        this.sendTimeout = sendTimeout;
        this.mSerialPort = mSerialPort;
    }

    @Override
    protected Object onRun(Handler handler) {
        try {
            writeDatas();
            if (!isReceive) {//超时
                if ((sendCount - 1) <= resendCount) {
                    writeDatas();
                } else {
                    sendMessage(0x123);
                }
            }
        } catch (Exception e) {
            isReceive = true;
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        onTimeout(what, sendDatas);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description writeDatas
     */
    private void writeDatas() throws InterruptedException {
        if (mSerialPort.writeSerialPort(sendDatas)) {
            sendCount++;
            Log.i(TAG, "指令-发送:[ " + XCByteUtil.byteToHexStr(sendDatas) + "],第" + sendCount + "次");
            waitReceive();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/9 13:05
     * Description：waitReceive
     */
    private void waitReceive() throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        while (!isReceive && System.currentTimeMillis() - currentTime < sendTimeout) {
            Thread.sleep(1);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 14:51
     * Description：receive
     */
    public void receive() {
        isReceive = true;
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
     * Time：2020/3/10 11:18
     * Description：onTimeout
     */
    public abstract void onTimeout(int what, byte[] sendDatas);
}
