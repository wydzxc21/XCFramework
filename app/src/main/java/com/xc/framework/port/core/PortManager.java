package com.xc.framework.port.core;

import android.util.Log;

import com.xc.framework.util.XCByteUtil;

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
    private List<OnPortSendListener> portSendListenerList;//发送监听集合
    private List<OnPortReceiveListener> portReceiveListenerList;//接收监听集合
    private PortReceiveThread mPortReceiveThread;//接收线程
    private ExecutorService queueSendPool;//队列发送线程池
    private ExecutorService freeSendPool;//自由发送线程池
    private boolean isOpen = false;//是否打开串口
    private boolean isClearSend;//是否清空发送
    private boolean isPauseReceive;//是否暂停接收

    public PortManager() {
        portSendListenerList = new ArrayList<OnPortSendListener>();
        portReceiveListenerList = new ArrayList<OnPortReceiveListener>();
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
        queueSendPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        freeSendPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
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
                clearSend(true);
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
        boolean isClose = false;
        clearSend(false);
        if (mPortReceiveThread != null) {
            mPortReceiveThread.stopThread();
            mPortReceiveThread = null;
        }
        if (getIPort() != null) {
            isClose = getIPort().closePort();
        }
        isOpen = !isClose;
        return isClose;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/5 8:42
     * Description：清空发送
     */
    public void clearSend() {
        clearSend(true);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/5 8:42
     * Description：清空发送
     */
    private void clearSend(boolean isInitPool) {
        isClearSend = true;
        if (queueSendPool != null) {
            queueSendPool.shutdownNow();
            queueSendPool = null;
        }
        if (freeSendPool != null) {
            freeSendPool.shutdownNow();
            freeSendPool = null;
        }
        if (mPortReceiveThread != null) {
            mPortReceiveThread.reset();
            mPortReceiveThread.clear();
        }
        if (isInitPool) {
            initPool();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/10 15:35
     * Description：暂停接收
     */
    public void pauseReceive() {
        isPauseReceive = true;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/10 15:35
     * Description：继续接收
     */
    public void continueReceive() {
        isPauseReceive = false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 9:25
     * Description：startReceivedTask
     */
    private void startReceivedThread() {
        mPortReceiveThread = new PortReceiveThread(getPortParam(), getIPort()) {
            @Override
            public void onResponse(byte[] responseDatas) {
                if (portReceiveListenerList != null && !portReceiveListenerList.isEmpty()) {
                    for (OnPortReceiveListener listener : portReceiveListenerList) {
                        if (listener != null) {
                            listener.onResponse(responseDatas);
                        }
                    }
                }
            }

            @Override
            public void onRequest(byte[] requestDatas) {
                if (portReceiveListenerList != null && !portReceiveListenerList.isEmpty()) {
                    for (OnPortReceiveListener listener : portReceiveListenerList) {
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
     * Time：2020/8/5 17:05
     * Description：串口发送-直接发送
     * Param：bytes 发送数据
     */
    public void sendDirect(final byte[] bytes) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getIPort().writePort(bytes);
                Log.i(TAG, "指令-直接发送:[" + XCByteUtil.toHexStr(bytes, true) + "]");
                if (portSendListenerList != null && !portSendListenerList.isEmpty()) {
                    for (OnPortSendListener listener : portSendListenerList) {
                        if (listener != null) {
                            listener.onSend(-1, bytes, 1);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Author：ZhangXuanChena
     * Time：2020/7/11 16:48
     * Description：串口发送-阻塞
     * Param：bytes 发送数据
     * Param：portSendType 发送类型
     * Param：portReceiveType 接收类型
     */
    public byte[] sendBlock(byte[] bytes, PortSendType portSendType, PortReceiveType portReceiveType) {
        return sendBlock(bytes, portSendType, portReceiveType, null);
    }

    /**
     * Author：ZhangXuanChena
     * Time：2020/7/11 16:48
     * Description：串口发送-阻塞
     * Param：bytes 发送数据
     * Param：portSendType 发送类型
     * Param：portReceiveType 接收类型
     * Param：portFilterCallback 接收过滤回调
     */
    public byte[] sendBlock(byte[] bytes, PortSendType portSendType, PortReceiveType portReceiveType, PortFilterCallback portFilterCallback) {
        isClearSend = false;
        if (queueSendPool == null || queueSendPool.isShutdown() || freeSendPool == null || freeSendPool.isShutdown()) {
            return null;
        }
        try {
            Future<byte[]> mFuture = null;
            if (portSendType == PortSendType.Queue) {
                mFuture = queueSendPool.submit(getPortSendCallable(bytes, portReceiveType, -1, null, portFilterCallback));
            } else if (portSendType == PortSendType.Free) {
                mFuture = freeSendPool.submit(getPortSendCallable(bytes, portReceiveType, -1, null, portFilterCallback));
            }
            return mFuture != null ? mFuture.get() : null;
        } catch (Exception e) {
            if (portSendListenerList != null && !portSendListenerList.isEmpty()) {
                for (OnPortSendListener listener : portSendListenerList) {
                    if (listener != null) {
                        listener.onError(bytes, e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送-异步
     * Param：bytes 发送数据
     * Param：portSendType 发送类型
     * Param：portReceiveType 接收类型
     * Param：what 区分消息
     * Param：portReceiveCallback 异步发送接收回调
     */
    public void sendAsync(byte[] bytes, PortSendType portSendType, PortReceiveType portReceiveType, int what, PortReceiveCallback portReceiveCallback) {
        sendAsync(bytes, portSendType, portReceiveType, what, portReceiveCallback, null);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 16:15
     * Description：串口发送-异步
     * Param：bytes 发送数据
     * Param：portSendType 发送类型
     * Param：portReceiveType 接收类型
     * Param：what 区分消息
     * Param：portReceiveCallback 异步发送接收回调
     * Param：portFilterCallback 接收过滤回调
     */
    public void sendAsync(byte[] bytes, PortSendType portSendType, PortReceiveType portReceiveType, int what, PortReceiveCallback portReceiveCallback, PortFilterCallback portFilterCallback) {
        isClearSend = false;
        if (queueSendPool == null || queueSendPool.isShutdown() || freeSendPool == null || freeSendPool.isShutdown()) {
            return;
        }
        if (portSendType == PortSendType.Queue) {
            queueSendPool.submit(getPortSendCallable(bytes, portReceiveType, what, portReceiveCallback, portFilterCallback));
        } else if (portSendType == PortSendType.Free) {
            freeSendPool.submit(getPortSendCallable(bytes, portReceiveType, what, portReceiveCallback, portFilterCallback));
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/9/4 16:38
     * Description：getPortSendCallable
     */
    private PortSendCallable getPortSendCallable(byte[] bytes, PortReceiveType portReceiveType, int what, final PortReceiveCallback portReceiveCallback, PortFilterCallback portFilterCallback) {
        PortSendCallable mPortSendCallable = new PortSendCallable(bytes, portReceiveType, what, portFilterCallback, getIPort(), getPortParam(), mPortReceiveThread) {
            @Override
            public void onResponse(int what, byte[] responseDatas) {
                if (portReceiveCallback != null) {
                    portReceiveCallback.onResponse(what, responseDatas);
                }
            }

            @Override
            public void onInterrupt(int what, byte[] interruptDatas) {
                if (portReceiveCallback != null) {
                    portReceiveCallback.onInterrupt(what, interruptDatas);
                }
            }

            @Override
            public void onTimeout(int what, byte[] sendDatas) {
                if (portReceiveCallback != null) {
                    portReceiveCallback.onTimeout(what, sendDatas);
                }
            }

            @Override
            public void onSend(int what, byte[] sendDatas, int sendCount) {
                if (portSendListenerList != null && !portSendListenerList.isEmpty()) {
                    for (OnPortSendListener listener : portSendListenerList) {
                        if (listener != null) {
                            listener.onSend(what, sendDatas, sendCount);
                        }
                    }
                }
            }

            @Override
            public boolean isClearSend() {
                return isClearSend;
            }

            @Override
            public boolean isPauseReceive() {
                return isPauseReceive;
            }
        };
        return mPortSendCallable;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置接收监听
     */
    public void setOnPortReceiveListener(OnPortReceiveListener onPortReceiveListener) {
        if (portReceiveListenerList != null) {
            portReceiveListenerList.add(onPortReceiveListener);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/14 12:26
     * Description：移除接收监听
     */
    public void removeOnPortReceiveListener(OnPortReceiveListener onPortReceiveListener) {
        if (portReceiveListenerList != null && !portReceiveListenerList.isEmpty()) {
            portReceiveListenerList.remove(onPortReceiveListener);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/14 12:26
     * Description：清空接收监听
     */
    public void clearOnPortReceiveListener() {
        if (portReceiveListenerList != null && !portReceiveListenerList.isEmpty()) {
            portReceiveListenerList.clear();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置发送监听
     */
    public void setOnPortSendListener(OnPortSendListener onPortSendListener) {
        if (portSendListenerList != null) {
            portSendListenerList.add(onPortSendListener);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/14 12:26
     * Description：移除发送监听
     */
    public void removeOnPortSendListener(OnPortSendListener onPortSendListener) {
        if (portSendListenerList != null && !portSendListenerList.isEmpty()) {
            portSendListenerList.remove(onPortSendListener);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/14 12:26
     * Description：清空发送监听
     */
    public void clearOnPortSendListener() {
        if (portSendListenerList != null && !portSendListenerList.isEmpty()) {
            portSendListenerList.clear();
        }
    }
}
