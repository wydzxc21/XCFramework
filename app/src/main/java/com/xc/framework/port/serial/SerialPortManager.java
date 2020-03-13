package com.xc.framework.port.serial;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：串口管理类
 */
public class SerialPortManager {
    private SerialPort mSerialPort;
    private SerialPortParam mSerialPortParam;
    private OnSerialPortListener onSerialPortListener;
    private boolean isOpen = false;
    private SerialPortReceiveThread mSerialPortReceiveThread;//接收线程
    private ThreadPoolExecutor mThreadPoolExecutor;//发送线程池
    //

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
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：devicePath 串口地址
     * Param：baudrate 波特率
     * Param：resendCount 重发次数，默认0
     * Param：sendTimeout 发送超时(毫秒)，默认2000
     * Param：receiveFrameHeads 接收帧头，默认null
     */
    public void init(String devicePath, int baudrate, int resendCount, int sendTimeout, byte[] receiveFrameHeads) {
        this.mSerialPortParam = new SerialPortParam(devicePath, baudrate);
        this.mSerialPortParam.setResendCount(resendCount);
        this.mSerialPortParam.setSendTimeout(sendTimeout);
        this.mSerialPortParam.setReceiveFrameHeads(receiveFrameHeads);
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
        mThreadPoolExecutor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1));
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
            if (isOpen) {
                initData();
                startReceivedThread();
            }
        }
        return isOpen;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:45
     * Description：串口关闭
     */
    public boolean close() {
        if (mSerialPort != null) {
            mSerialPort.closeSerialPort();
            mSerialPort = null;
        }
        if (mThreadPoolExecutor != null) {
            mThreadPoolExecutor.shutdownNow();
            mThreadPoolExecutor = null;
        }
        if (mSerialPortReceiveThread != null) {
            mSerialPortReceiveThread.stopThread();
            mSerialPortReceiveThread = null;
        }
        if (onSerialPortListener != null) {
            onSerialPortListener = null;
        }
        isOpen = false;
        return true;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 9:25
     * Description：startReceivedTask
     */
    private void startReceivedThread() {
        mSerialPortReceiveThread = new SerialPortReceiveThread(mSerialPortParam, mSerialPort) {
            @Override
            public int setLength(byte[] receiveDatas) {
                if (onSerialPortListener != null) {
                    return onSerialPortListener.setLength(receiveDatas);
                }
                return 0;
            }

            @Override
            public void onReceive(byte[] receiveDatas) {
                if (onSerialPortListener != null) {
                    int what = 0;
                    SerialPortSendRunnable mSerialPortSendRunnable = (SerialPortSendRunnable) mThreadPoolExecutor.getQueue().poll();
                    if (mSerialPortSendRunnable != null) {
                        what = mSerialPortSendRunnable.getWhat();
                        mSerialPortSendRunnable.receive();
                    }
                    onSerialPortListener.onReceive(what, receiveDatas);
                }
            }
        };
        mSerialPortReceiveThread.setDaemon(true);
        mSerialPortReceiveThread.startThread();
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送
     */

    public void send(byte[] bytes, int what) {
        startSendThread(bytes, what);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 13:12
     * Description：startSendTask
     */
    private void startSendThread(byte[] bytes, int what) {
        mThreadPoolExecutor.execute(new SerialPortSendRunnable(bytes, what, mSerialPortParam, mSerialPort) {
            @Override
            public void onTimeout(int what, byte[] sendDatas) {
                if (onSerialPortListener != null) {
                    onSerialPortListener.onTimeout(what, sendDatas);
                }
            }
        });
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
