package com.xc.framework.port.usb;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCRunnable;
import com.xc.framework.util.XCByteUtil;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：usb发送
 */
public abstract class UsbPortSendRunnable extends XCRunnable {
    private final String TAG = "SerialPortSendRunnable";
    private byte[] sendDatas;//发送数据
    private int what;
    private int resendCount;//重发次数
    private int sendTimeout;//发送超时(毫秒)
    private UsbPort usbPort;
    private LinkedBlockingQueue linkedBlockingQueue;
    //
    boolean isRelease;//是否释放
    int sendCount;//发送次数

    /**
     * @param sendDatas           发送数据
     * @param what                区分消息
     * @param usbPortParam        usb参数
     * @param usbPort             usb工具
     * @param linkedBlockingQueue 任务队列
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public UsbPortSendRunnable(byte[] sendDatas, int what, UsbPortParam usbPortParam, UsbPort usbPort, LinkedBlockingQueue linkedBlockingQueue) {
        this.sendDatas = sendDatas;
        this.what = what;
        this.resendCount = usbPortParam.getResendCount();
        this.sendTimeout = usbPortParam.getSendTimeout();
        this.usbPort = usbPort;
        this.linkedBlockingQueue = linkedBlockingQueue;
    }

    @Override
    protected Object onRun(Handler handler) {
        try {
            linkedBlockingQueue.put(this);
            writeDatas();
            if (!isRelease) {//超时
                if ((sendCount - 1) <= resendCount) {
                    writeDatas();
                } else {
                    sendMessage(0x123);
                }
            }
        } catch (Exception e) {
            isRelease = true;
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
        if (usbPort.writeUsbPort(sendDatas)) {
            sendCount++;
            Log.i(TAG, "指令-发送:[" + XCByteUtil.byteToHexStr(sendDatas, true) + "],第" + sendCount + "次");
            onSend(what, sendDatas, sendCount);
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
        while (!isRelease && System.currentTimeMillis() - currentTime < sendTimeout) {
            Thread.sleep(1);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 14:51
     * Description：release
     */
    public void release() {
        isRelease = true;
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
     * Time：2020/5/11 8:38
     * Description：onSend
     */
    public abstract void onSend(int what, byte[] sendDatas, int sendCount);

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 11:18
     * Description：onTimeout
     */
    public abstract void onTimeout(int what, byte[] sendDatas);
}
