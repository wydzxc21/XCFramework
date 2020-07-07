package com.xc.framework.port.serial;

import com.xc.framework.port.core.LengthCallback;
import com.xc.framework.port.core.OnInterruptListener;
import com.xc.framework.port.core.ReceiveCallback;

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
    private final String TAG = "SerialPortManager";
    private SerialPort mSerialPort;
    private SerialPortParam mSerialPortParam;
    private OnInterruptListener onInterruptListener;//中断监听
    private SerialPortReceiveThread mSerialPortReceiveThread;//接收线程
    private ExecutorService mExecutorService;//发送线程池
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
     * Param：lengthCallback 设置长度回调，默认null
     */
    public void init(String devicePath, int baudrate, int resendCount, int sendTimeout, byte[] receiveFrameHeads, byte[] interruptFrameHeads, LengthCallback lengthCallback) {
        this.mSerialPortParam = new SerialPortParam(devicePath, baudrate);
        this.mSerialPortParam.setResendCount(resendCount);
        this.mSerialPortParam.setSendTimeout(sendTimeout);
        this.mSerialPortParam.setReceiveFrameHeads(receiveFrameHeads);
        this.mSerialPortParam.setInterruptFrameHeads(interruptFrameHeads);
        this.mSerialPortParam.setLengthCallback(lengthCallback);
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
        if (mSerialPortReceiveThread != null) {
            mSerialPortReceiveThread.stopThread();
            mSerialPortReceiveThread = null;
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
                if (mSerialPortParam.getLengthCallback() != null) {
                    return mSerialPortParam.getLengthCallback().onLength(receiveOrInterruptDatas);
                }
                return 0;
            }

            @Override
            public void onInterrupt(byte[] interruptDatas) {
                if (onInterruptListener != null) {
                    onInterruptListener.onInterrupt(interruptDatas);
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
    public void send(byte[] bytes, int what, boolean isWaitReceive, final ReceiveCallback receiveCallback) {
        if (mExecutorService == null || mExecutorService.isShutdown()) {
            return;
        }
        mExecutorService.execute(new SerialPortSendRunnable(bytes, what, isWaitReceive, mSerialPortParam, mSerialPort, mSerialPortReceiveThread) {
            @Override
            public void onReceive(int what, byte[] receiveDatas) {
                if (receiveCallback != null) {
                    receiveCallback.onReceive(what, receiveDatas);
                }
            }

            @Override
            public void onTimeout(int what, byte[] sendDatas) {
                if (receiveCallback != null) {
                    receiveCallback.onTimeout(what, sendDatas);
                }
            }
        });
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置中断监听
     */
    public void setOnInterruptListener(OnInterruptListener onInterruptListener) {
        this.onInterruptListener = onInterruptListener;
    }

}
