package com.xc.framework.port.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Date：2019/11/25
 * Author：ZhangXuanChen
 * Description：usb管理类
 */
public class UsbPortManager {
    private Context mContext;
    private UsbPort mUsbPort;
    private UsbPortParam mUsbPortParam;
    private OnUsbPortListener onUsbPortListener;
    private UsbPortReceiveThread mUsbPortReceiveThread;//接收线程
    private ExecutorService mExecutorService;//发送线程池
    private LinkedBlockingQueue<UsbPortSendRunnable> mLinkedBlockingQueue;//正在执行的发送线程
    private boolean isOpen = false;

    public UsbPortManager(Context context) {
        mContext = context;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 15:30
     * Description：初始化串口
     * Param：SerialPortParam usb参数
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
     * Time：2019/11/25 15:30
     * Description：初始化usb
     * Param：context 上下文
     * Param：vid 厂商id
     * Param：pid 设备id
     * Param：baudrate 波特率
     * Param：resendCount 重发次数，默认0
     * Param：sendTimeout 发送超时(毫秒)，默认2000
     * Param：receiveFrameHeads 接收帧头，默认null
     * Param：interruptFrameHeads 中断帧头，默认null
     */
    public void init(int vid, int pid, int baudrate, int resendCount, int sendTimeout, byte[] receiveFrameHeads, byte[] interruptFrameHeads) {
        this.mUsbPortParam = new UsbPortParam(mContext, vid, pid, baudrate);
        this.mUsbPortParam.setResendCount(resendCount);
        this.mUsbPortParam.setSendTimeout(sendTimeout);
        this.mUsbPortParam.setReceiveFrameHeads(receiveFrameHeads);
        this.mUsbPortParam.setInterruptFrameHeads(interruptFrameHeads);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 11:01
     * Description：initData
     */
    private void initData() {
        mUsbPort = new UsbPort(mContext);
        initPool();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/27 13:25
     * Description：initPool
     */
    private void initPool() {
        mLinkedBlockingQueue = new LinkedBlockingQueue<UsbPortSendRunnable>(1);
        mExecutorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/25 16:01
     * Description：串口打开
     * Return：boolean
     */
    public boolean open() {
        if (mUsbPort != null && mUsbPortParam != null) {
            isOpen = mUsbPort.openUsbPort(mUsbPortParam);
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
        if (mUsbPort != null) {
            mUsbPort.closeUsbPort();
            mUsbPort = null;
        }
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
        if (mLinkedBlockingQueue != null) {
            mLinkedBlockingQueue.clear();
            mLinkedBlockingQueue = null;
        }
        if (mUsbPortReceiveThread != null) {
            mUsbPortReceiveThread.stopThread();
            mUsbPortReceiveThread = null;
        }
        if (onUsbPortListener != null) {
            onUsbPortListener = null;
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
        mUsbPortReceiveThread = new UsbPortReceiveThread(mUsbPortParam, mUsbPort) {
            @Override
            public int setLength(byte[] receiveOrInterruptDatas) {
                if (onUsbPortListener != null) {
                    return onUsbPortListener.setLength(receiveOrInterruptDatas);
                }
                return 0;
            }

            @Override
            public void onReceive(byte[] receiveDatas) {
                if (onUsbPortListener != null) {
                    onUsbPortListener.onReceive(receiveSendRunnable(), receiveDatas);
                }
            }

            @Override
            public void onInterrupt(byte[] interruptDatas) {
                if (onUsbPortListener != null) {
                    onUsbPortListener.onInterrupt(receiveSendRunnable(), interruptDatas);
                }
            }
        };
        mUsbPortReceiveThread.setDaemon(true);
        mUsbPortReceiveThread.startThread();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/29 11:44
     * Description：释放发送任务
     */
    private int receiveSendRunnable() {
        int what = 0;
        UsbPortSendRunnable sendRunnable = mLinkedBlockingQueue.poll();
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
        mExecutorService.execute(new UsbPortSendRunnable(bytes, what, mUsbPortParam, mUsbPort, mLinkedBlockingQueue) {
            @Override
            public void onSend(int what, byte[] sendDatas, int sendCount) {
                if (mUsbPortReceiveThread != null) {
                    mUsbPortReceiveThread.reset();
                }
                if (onUsbPortListener != null) {
                    onUsbPortListener.onSend(what, sendDatas, sendCount);
                }
            }

            @Override
            public void onTimeout(int what, byte[] sendDatas) {
                if (onUsbPortListener != null) {
                    onUsbPortListener.onTimeout(what, sendDatas);
                }
            }
        });
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置接收监听
     */
    public void setOnUsbPortListener(OnUsbPortListener onUsbPortListener) {
        this.onUsbPortListener = onUsbPortListener;
    }

}
