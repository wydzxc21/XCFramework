package com.xc.framework.port.serial;

import com.xc.framework.port.core.LengthCallback;
import com.xc.framework.port.core.OnReceiveRequestListener;
import com.xc.framework.port.core.ReceiveResponseCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    private List<OnReceiveRequestListener> receiveRequestListenerList;//接收请求监听集合
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
     * Param：receiveResponseFrameHeads 接收响应帧头，默认null
     * Param：receiveRequestFrameHeads 接收请求帧头，默认null
     * Param：lengthCallback 设置长度回调，默认null
     */
    public void init(String devicePath, int baudrate, int resendCount, int sendTimeout, byte[] receiveResponseFrameHeads, byte[] receiveRequestFrameHeads, LengthCallback lengthCallback) {
        this.mSerialPortParam = new SerialPortParam(devicePath, baudrate);
        this.mSerialPortParam.setResendCount(resendCount);
        this.mSerialPortParam.setSendTimeout(sendTimeout);
        this.mSerialPortParam.setReceiveResponseFrameHeads(receiveResponseFrameHeads);
        this.mSerialPortParam.setReceiveRequestFrameHeads(receiveRequestFrameHeads);
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
        receiveRequestListenerList = new ArrayList<OnReceiveRequestListener>();
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
            public int setLength(byte[] receiveDatas) {
                if (mSerialPortParam.getLengthCallback() != null) {
                    return mSerialPortParam.getLengthCallback().onLength(receiveDatas);
                }
                return 0;
            }

            @Override
            public void onRequest(byte[] requestDatas) {
                if (receiveRequestListenerList != null && !receiveRequestListenerList.isEmpty()) {
                    for (OnReceiveRequestListener listener : receiveRequestListenerList) {
                        if (listener != null) {
                            listener.onRequest(requestDatas);
                        }
                    }
                }
            }
        };
        mSerialPortReceiveThread.setDaemon(true);
        mSerialPortReceiveThread.startThread();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/11 16:48
     * Description：串口发送-阻塞
     */
    private byte[] send(byte[] bytes, int what, boolean isWaitResponse) {
        return send(bytes, what, isWaitResponse, true, null);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送-异步
     */
    private void send(byte[] bytes, int what, boolean isWaitResponse, ReceiveResponseCallback receiveResponseCallback) {
        send(bytes, what, isWaitResponse, false, receiveResponseCallback);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送
     */
    private byte[] send(byte[] bytes, int what, boolean isWaitResponse, boolean isBlockSend, final ReceiveResponseCallback receiveResponseCallback) {
        if (mExecutorService == null || mExecutorService.isShutdown()) {
            return null;
        }
        try {
            Future<byte[]> mFuture = mExecutorService.submit(new SerialPortSendCallable(bytes, what, isWaitResponse, mSerialPortParam, mSerialPort, mSerialPortReceiveThread) {
                @Override
                public void onResponse(int what, byte[] responseDatas) {
                    if (receiveResponseCallback != null) {
                        receiveResponseCallback.onResponse(what, responseDatas);
                    }
                }

                @Override
                public void onTimeout(int what, byte[] sendDatas) {
                    if (receiveResponseCallback != null) {
                        receiveResponseCallback.onTimeout(what, sendDatas);
                    }
                }
            });
            if (isBlockSend) {
                return mFuture.get();
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置接收请求监听
     */
    public void setOnReceiveRequestListener(OnReceiveRequestListener onReceiveRequestListener) {
        if (receiveRequestListenerList != null) {
            receiveRequestListenerList.add(onReceiveRequestListener);
        }
    }

}
