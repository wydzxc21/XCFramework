package com.xc.framework.port.core;

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
 * Description：串口管理基类
 */
public abstract class PortManager {
    private final String TAG = "PortManager";
    private List<OnReceiveRequestListener> receiveRequestListenerList;//接收请求监听集合
    private PortReceiveThread mPortReceiveThread;//接收线程
    private ExecutorService mExecutorService;//发送线程池
    private boolean isOpen = false;

    public PortManager() {
        receiveRequestListenerList = new ArrayList<OnReceiveRequestListener>();
        initPool();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：IPort
     */
    public abstract IPort getIPort();

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：PortParam
     */
    public abstract PortParam getPortParam();

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
        if (getIPort() != null && getPortParam() != null) {
            isOpen = getIPort().openPort(getPortParam());
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
        if (getIPort() != null) {
            getIPort().closePort();
        }
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
        if (mPortReceiveThread != null) {
            mPortReceiveThread.stopThread();
            mPortReceiveThread = null;
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
        mPortReceiveThread = new PortReceiveThread(getPortParam(), getIPort()) {
            @Override
            public int setLength(byte[] receiveDatas) {
                if (getPortParam().getLengthCallback() != null) {
                    return getPortParam().getLengthCallback().onLength(receiveDatas);
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
        mPortReceiveThread.setDaemon(true);
        mPortReceiveThread.startThread();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/11 16:48
     * Description：串口发送-阻塞
     * Param：bytes 发送数据
     * Param：isWaitResponse 是否等待响应
     */
    public byte[] send(byte[] bytes, boolean isWaitResponse) {
        return send(bytes, isWaitResponse, true, 0x123, null);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送-异步
     * Param：bytes 发送数据
     * Param：isWaitResponse 是否等待响应
     * Param：what 区分消息
     * Param：receiveResponseCallback 异步发送接收回调
     */
    public void send(byte[] bytes, boolean isWaitResponse, int what, ReceiveResponseCallback receiveResponseCallback) {
        send(bytes, isWaitResponse, false, what, receiveResponseCallback);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送
     * Param：bytes 发送数据
     * Param：isWaitResponse 是否等待响应
     * Param：isBlockSend 是否阻塞发送
     * Param：what 区分消息
     * Param：receiveResponseCallback 异步发送接收回调
     */
    private byte[] send(byte[] bytes, boolean isWaitResponse, boolean isBlockSend, int what, final ReceiveResponseCallback receiveResponseCallback) {
        if (mExecutorService == null || mExecutorService.isShutdown()) {
            return null;
        }
        try {
            Future<byte[]> mFuture = mExecutorService.submit(new PortSendCallable(bytes, isWaitResponse, what, getPortParam(), getIPort(), mPortReceiveThread) {
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
