package com.xc.framework.port.serial;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：串口助手
 */
public class SerialPortHelper {
    private SerialPort mSerialPort;
    private SerialPortParam mSerialPortParam;
    private SerialPortReceivedThread mSerialPortReceivedThread;
    private SerialPortSendThread mSerialPortSendThread;
    private boolean isOpen = false;

//    public static SerialPortHelper getInstance() {
//        if (mSerialPortHelper == null) {
//            mSerialPortHelper = new SerialPortHelper();
//        }
//        return mSerialPortHelper;
//    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：SerialPortParam 串口参数
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
     * Time：2019/11/25 13:17
     * Description：打开串口
     *
     * @param suPath     su路径，默认：/system/bin/su
     * @param devicePath 串口地址
     * @param baudrate   波特率
     * @param dataBits   数据位，默认8
     * @param stopBits   停止位，默认1
     * @param parity     奇偶校验位，默认0（无校验）
     * @param flowCon    流控，默认0（不使用）
     */
    public void init(String suPath, String devicePath, int baudrate, int dataBits, int stopBits, int parity, int flowCon) {
        this.mSerialPortParam = new SerialPortParam(suPath, devicePath, baudrate, dataBits, stopBits, parity, flowCon);
        initData();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 11:01
     * Description：initData
     */
    private void initData() {
        if (mSerialPort == null) {
            mSerialPort = new SerialPort();
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
            if (mSerialPort != null && mSerialPortParam != null) {
                isOpen = mSerialPort.openSerialPort(mSerialPortParam);
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
            if (mSerialPort != null) {
                isClose = mSerialPort.closeSerialPort();
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
        mSerialPortReceivedThread = new SerialPortReceivedThread(mSerialPort) {
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
        mSerialPortSendThread = new SerialPortSendThread(mSerialPort, bytes);
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
