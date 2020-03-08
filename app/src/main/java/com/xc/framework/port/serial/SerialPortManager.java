package com.xc.framework.port.serial;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：串口助手
 */
public class SerialPortManager {
    private SerialPort mSerialPort;
    private SerialPortParam mSerialPortParam;
    private ExecutorService mExecutorService;
    private boolean isOpen = false;
    private OnSerialPortListener onSerialPortListener;

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
     * Time：2019/11/26 11:01
     * Description：initData
     */
    private void initData() {
        if (mSerialPort == null) {
            mSerialPort = new SerialPort();
            mExecutorService = Executors.newSingleThreadExecutor();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 16:01
     * Description：串口打开
     * Return：boolean
     */
    public boolean open() {
        if (mSerialPort != null && mSerialPortParam != null) {
            isOpen = mSerialPort.openSerialPort(mSerialPortParam);
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
        if (mSerialPort != null) {
            isClose = mSerialPort.closeSerialPort();
        }
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
        if (onSerialPortListener != null) {
            onSerialPortListener = null;
        }
        isOpen = !isClose;
        return isClose;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送
     */
    public void send(byte[] bytes, int what) {
        if (mSerialPortParam != null) {
            send(bytes, what, mSerialPortParam.getRetryCount(), mSerialPortParam.getTimeout(), mSerialPortParam.getFrameHeaders());
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送
     */
    public void send(byte[] bytes, int what, int retryCount, int timeout, byte[] frameHeaders) {
        if (mExecutorService != null && mSerialPort != null && bytes != null && bytes.length > 0) {
            mExecutorService.execute(new SerialPortRunnable(bytes, what, retryCount, timeout, frameHeaders, mSerialPort, onSerialPortListener));
        }
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置接收监听
     */
    public void setOnSerialPortListener(OnSerialPortListener onSerialPortListener) {
        this.onSerialPortListener = onSerialPortListener;
    }

}
