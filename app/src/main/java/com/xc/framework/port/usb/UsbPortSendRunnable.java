package com.xc.framework.port.usb;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCRunnable;
import com.xc.framework.util.XCByteUtil;


/**
 * Date：2020/3/6
 * Author：ZhangXuanChen
 * Description：usb发送
 */
public abstract class UsbPortSendRunnable extends XCRunnable {
    private final String TAG = "SerialPortSendRunnable";
    private byte[] sendDatas;//发送数据
    private int what;
    private boolean isWaitReceive;//是否等待接收
    private int resendCount;//重发次数
    private int sendTimeout;//发送超时(毫秒)
    private UsbPort usbPort;
    private UsbPortReceiveThread usbPortReceiveThread;
    //
    boolean isReceive;//是否接收
    int sendCount;//发送次数

    /**
     * @param sendDatas            发送数据
     * @param what                 区分消息
     * @param isWaitReceive        是否等待接收
     * @param usbPortParam         usb参数
     * @param usbPort              usb工具
     * @param usbPortReceiveThread 接收线程
     * @author ZhangXuanChen
     * @date 2020/3/8
     */
    public UsbPortSendRunnable(byte[] sendDatas, int what, boolean isWaitReceive, UsbPortParam usbPortParam, UsbPort usbPort, UsbPortReceiveThread usbPortReceiveThread) {
        this.sendDatas = sendDatas;
        this.what = what;
        this.isWaitReceive = isWaitReceive;
        this.resendCount = usbPortParam.getResendCount();
        this.sendTimeout = usbPortParam.getSendTimeout();
        this.usbPort = usbPort;
        this.usbPortReceiveThread = usbPortReceiveThread;
    }

    @Override
    protected Object onRun(Handler handler) {
        try {
            writeDatas();
            if (!isReceive && isWaitReceive) {//超时
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
        switch (msg.what) {
            case 0x123://超时
                onTimeout(what, sendDatas);
                break;
            case 0x234://接收
                onReceive(what, (byte[]) msg.obj);
                break;
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description writeDatas
     */
    private void writeDatas() throws InterruptedException {
        usbPortReceiveThread.reset();
        if (usbPort.writeUsbPort(sendDatas)) {
            sendCount++;
            Log.i(TAG, "指令-发送:[" + XCByteUtil.byteToHexStr(sendDatas, true) + "],第" + sendCount + "次");
            if (isWaitReceive) {//是否等待接收
                waitReceive();
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/9 13:05
     * Description：waitReceive
     */
    private void waitReceive() throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        do {
            byte[] receiveDatas = usbPortReceiveThread.getReceive();
            if (receiveDatas != null && receiveDatas.length > 0) {
                isReceive = true;
                sendMessage(0x234, receiveDatas);
            }
            Thread.sleep(1);
        } while (!isReceive && System.currentTimeMillis() - currentTime < sendTimeout);
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
     * Description：onReceive
     */
    public abstract void onReceive(int what, byte[] receiveDatas);

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 11:18
     * Description：onTimeout
     */
    public abstract void onTimeout(int what, byte[] sendDatas);
}
