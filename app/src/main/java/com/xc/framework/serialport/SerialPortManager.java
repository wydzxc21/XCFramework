package com.xc.framework.serialport;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：SerialPortManager
 */
public class SerialPortManager {
    SerialPortHelper mSerialPortHelper;
    SerialPortParam mSerialPortParam;
    SerialPortReceivedThread mSerialPortReceivedThread;
    SerialPortSendThread mSerialPortSendThread;
    boolean isOpen = false;

//    public static SerialPortManager getInstance() {
//        if (mSerialPortHelper == null) {
//            mSerialPortHelper = new SerialPortManager();
//        }
//        return mSerialPortHelper;
//    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：builder 构建参数
     */
    public void init(SerialPortParam serialPortParam) {
        this.mSerialPortParam = serialPortParam;
        initData();
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：devicePath 串口地址
     * Param：baudrate 波特率
     */
    public void init(String devicePath, int baudrate) {
        this.mSerialPortParam = new SerialPortParam(devicePath, baudrate);
        initData();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 11:01
     * Description：initData
     */
    private void initData() {
        if (mSerialPortHelper == null) {
            mSerialPortHelper = new SerialPortHelper();
        }
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
     * Time：2019/11/25 16:01
     * Description：串口打开
     * Return：boolean
     */
    public boolean open() {
        try {
            if (mSerialPortHelper != null && mSerialPortParam != null) {
                isOpen = mSerialPortHelper.openSerialPort(mSerialPortParam);
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
            if (mSerialPortHelper != null) {
                isClose = mSerialPortHelper.closeSerialPort();
                if (isClose) {
                    stopReceivedThread();
                    stopSendThread();
                    onSerialPortReceiveListener = null;
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
     * Time：2019/11/27 15:21
     * Description：startReceivedThread
     */
    private void startReceivedThread() {
        mSerialPortReceivedThread = new SerialPortReceivedThread(mSerialPortHelper.getInputStream()) {
            @Override
            public void onReceive(byte[] bytes) {
                if (onSerialPortReceiveListener != null) {
                    onSerialPortReceiveListener.onReceive(bytes);
                }
            }
        };
        mSerialPortReceivedThread.startThread();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:22
     * Description：stopReceivedThread
     */
    private void stopReceivedThread() {
        if (mSerialPortReceivedThread != null) {
            mSerialPortReceivedThread.stopThread();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:21
     * Description：startSendThread
     */
    private void startSendThread(byte[] bytes) {
        mSerialPortSendThread = new SerialPortSendThread(mSerialPortHelper.getOutputStream(), bytes);
        mSerialPortSendThread.startThread();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:22
     * Description：stopSendThread
     */
    private void stopSendThread() {
        if (mSerialPortSendThread != null) {
            mSerialPortSendThread.stopThread();
        }
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置接收监听
     */
    public void setOnSerialPortReceiveListener(OnSerialPortReceiveListener onSerialPortReceiveListener) {
        this.onSerialPortReceiveListener = onSerialPortReceiveListener;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：接口引用
     */
    OnSerialPortReceiveListener onSerialPortReceiveListener;

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:06
     * Description：OnSerialPortReceiveListener
     */
    public interface OnSerialPortReceiveListener {
        void onReceive(byte[] buffer);
    }
}
