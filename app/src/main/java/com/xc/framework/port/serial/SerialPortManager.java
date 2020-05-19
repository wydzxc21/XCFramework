package com.xc.framework.port.serial;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private SerialPortReceiveThread mSerialPortReceiveThread;//接收线程
    private ExecutorService mExecutorService;//发送线程池
    private LinkedBlockingQueue<SerialPortSendRunnable> mLinkedBlockingQueue;//正在执行的发送线程
    private boolean isOpen = false;

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
     * Param：interruptFrameHeads 中断帧头，默认null
     */
    public void init(String devicePath, int baudrate, int resendCount, int sendTimeout, byte[] receiveFrameHeads, byte[] interruptFrameHeads) {
        this.mSerialPortParam = new SerialPortParam(devicePath, baudrate);
        this.mSerialPortParam.setResendCount(resendCount);
        this.mSerialPortParam.setSendTimeout(sendTimeout);
        this.mSerialPortParam.setReceiveFrameHeads(receiveFrameHeads);
        this.mSerialPortParam.setInterruptFrameHeads(interruptFrameHeads);
        initData();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 11:01
     * Description：initData
     */
    private void initData() {
        mSerialPort = new SerialPort();
        initPool();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/27 13:25
     * Description：initPool
     */
    private void initPool() {
        mLinkedBlockingQueue = new LinkedBlockingQueue<SerialPortSendRunnable>(1);
        mExecutorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
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
                initPool();
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
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
        if (mLinkedBlockingQueue != null) {
            mLinkedBlockingQueue.clear();
            mLinkedBlockingQueue = null;
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
            public int setLength(byte[] receiveOrInterruptDatas) {
                if (onSerialPortListener != null) {
                    return onSerialPortListener.setLength(receiveOrInterruptDatas);
                }
                return 0;
            }

            @Override
            public void onReceive(byte[] receiveDatas) {
                if (onSerialPortListener != null) {
                    onSerialPortListener.onReceive(receiveSendRunnable(), receiveDatas);
                }
            }

            @Override
            public void onInterrupt(byte[] interruptDatas) {
                if (onSerialPortListener != null) {
                    onSerialPortListener.onInterrupt(receiveSendRunnable(), interruptDatas);
                }
            }

        };
        mSerialPortReceiveThread.setDaemon(true);
        mSerialPortReceiveThread.startThread();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/29 11:44
     * Description：释放发送任务
     */
    private int receiveSendRunnable() {
        int what = 0;
        SerialPortSendRunnable sendRunnable = mLinkedBlockingQueue.poll();
        if (sendRunnable != null) {
            what = sendRunnable.getWhat();
            sendRunnable.release();
        }
        return what;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送
     */
    public void send(byte[] bytes, int what) {
        if (mExecutorService == null || mExecutorService.isShutdown()) {
            return;
        }
        mExecutorService.execute(new SerialPortSendRunnable(bytes, what, mSerialPortParam, mSerialPort, mLinkedBlockingQueue) {

            @Override
            public void onSend(int what, byte[] sendDatas, int sendCount) {
                if (mSerialPortReceiveThread != null) {
                    mSerialPortReceiveThread.reset();
                }
                if (onSerialPortListener != null) {
                    onSerialPortListener.onSend(what, sendDatas, sendCount);
                }
            }

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
