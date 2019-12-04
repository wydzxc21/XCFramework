package com.xc.framework.port.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：串口助手
 */
public class UsbPortHelper {
    private Context mContext;
    private UsbPort mUsbPort;
    private UsbPortParam mUsbPortParam;
    private UsbPortReceivedThread mUsbPortReceivedThread;
    private UsbPortSendThread mUsbPortSendThread;
    private boolean isOpen = false;

    public UsbPortHelper(Context context) {
        this.mContext = context;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：UsbPortParam usb参数
     */
    public void init(UsbPortParam usbPortParam) {
        this.mUsbPortParam = usbPortParam;
        initData();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化usb
     * Param：UsbDevice usb设备
     * Param：baudrate 波特率
     */
    public void init(UsbDevice usbDevice, int baudrate) {
        this.mUsbPortParam = new UsbPortParam(usbDevice, baudrate);
        initData();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/4 13:41
     * Description：初始化usb
     * Param：context 上下文
     * Param：vid 厂商id
     * Param：pid 设备id
     * Param：baudrate 波特率
     */
    public void init(int vid, int pid, int baudrate) {
        this.mUsbPortParam = new UsbPortParam(mContext, vid, pid, baudrate);
        initData();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 13:17
     * Description：打开串口
     *
     * @param vid      厂商id
     * @param pid      设备id
     * @param baudrate 波特率
     * @param dataBits 数据位，默认8
     * @param stopBits 停止位，默认1
     * @param parity   奇偶校验位，默认0（无校验）
     */
    public void init(int vid, int pid, int baudrate, int dataBits, int stopBits, int parity) {
        this.mUsbPortParam = new UsbPortParam(mContext, vid, pid, baudrate, dataBits, stopBits, parity);
        initData();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 11:01
     * Description：initData
     */
    private void initData() {
        if (mUsbPort == null) {
            mUsbPort = new UsbPort(mContext);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 16:01
     * Description：串口打开
     * Return：boolean
     */
    public boolean open() {
        try {
            if (mUsbPort != null && mUsbPortParam != null) {
                isOpen = mUsbPort.openUsbPort(mUsbPortParam);
                if (isOpen) {
                    startReceivedThread();
                }
            }
        } catch (Exception e) {
            isOpen = false;
        }
        return isOpen;
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:45
     * Description：串口关闭
     */
    public boolean close() {
        boolean isClose = false;
        try {
            if (mUsbPort != null) {
                isClose = mUsbPort.closeUsbPort();
                if (isClose) {
                    stopReceivedThread();
                    stopSendThread();
                    onUsbPortReceiveListener = null;
                }
            }
        } catch (Exception e) {
            isClose = false;
        }
        isOpen = !isClose;
        return isClose;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送
     */
    public void send(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            startSendThread(bytes);
        }
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:21
     * Description：startReceivedThread
     */
    private void startReceivedThread() {
        mUsbPortReceivedThread = new UsbPortReceivedThread(mUsbPort) {
            @Override
            public void onReceive(byte[] bytes) {
                if (onUsbPortReceiveListener != null) {
                    onUsbPortReceiveListener.onReceive(bytes);
                }
            }
        };
        mUsbPortReceivedThread.startThread();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:22
     * Description：stopReceivedThread
     */
    private void stopReceivedThread() {
        if (mUsbPortReceivedThread != null) {
            mUsbPortReceivedThread.stopThread();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:21
     * Description：startSendThread
     */
    private void startSendThread(byte[] bytes) {
        mUsbPortSendThread = new UsbPortSendThread(mUsbPort, bytes);
        mUsbPortSendThread.startThread();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:22
     * Description：stopSendThread
     */
    private void stopSendThread() {
        if (mUsbPortSendThread != null) {
            mUsbPortSendThread.stopThread();
        }
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置接收监听
     */
    public void setOnUsbPortReceiveListener(OnUsbPortReceiveListener onUsbPortReceiveListener) {
        this.onUsbPortReceiveListener = onUsbPortReceiveListener;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：接口引用
     */
    OnUsbPortReceiveListener onUsbPortReceiveListener;

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:06
     * Description：OnSerialPortReceiveListener
     */
    public interface OnUsbPortReceiveListener {
        void onReceive(byte[] buffer);
    }
}
