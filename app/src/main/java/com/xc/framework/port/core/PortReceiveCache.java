package com.xc.framework.port.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2021/3/26
 * Author：ZhangXuanChen
 * Description：串口接收缓存
 */
public class PortReceiveCache {
    private final String TAG = "PortReceiveCache";
    private static final Object mLock = new Object();
    public static PortReceiveCache mPostReceiveBroadcast;
    private final ArrayList<byte[]> responseList = new ArrayList<byte[]>();
    private final ArrayList<byte[]> interruptList = new ArrayList<byte[]>();

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：getInstance
     */
    public static PortReceiveCache getInstance() {
        synchronized (mLock) {
            if (mPostReceiveBroadcast == null) {
                mPostReceiveBroadcast = new PortReceiveCache();
            }
        }
        return mPostReceiveBroadcast;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：PortReceiveCache
     */
    public PortReceiveCache() {

    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResponse
     */
    public void addResponse(byte[] bytes) {
        synchronized (responseList) {
            responseList.add(bytes);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:18
     * Description：getResponseList
     */
    public ArrayList<byte[]> getResponseList() {
        synchronized (responseList) {
            return responseList;
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 12:13
     * Description：removeResponse
     */
    public void removeResponse(byte[] bytes) {
        synchronized (responseList) {
            PortFrameUtil.remove(bytes, responseList);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:35
     * Description：clearResponse
     */
    public void clearResponse() {
        synchronized (responseList) {
            responseList.clear();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addInterrupt
     */
    public void addInterrupt(byte[] bytes) {
        synchronized (interruptList) {
            interruptList.add(bytes);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:19
     * Description：getInterruptList
     */
    public ArrayList<byte[]> getInterruptList() {
        synchronized (interruptList) {
            return interruptList;
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 12:13
     * Description：removeInterrupt
     */
    public void removeInterrupt(byte[] bytes) {
        synchronized (interruptList) {
            PortFrameUtil.remove(bytes, interruptList);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clearInterrupt
     */
    public void clearInterrupt() {
        synchronized (interruptList) {
            interruptList.clear();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:47
     * Description：getReceiveDatas
     */
    public byte[] getReceiveDatas(List<byte[]> receiveList, byte[] sendDatas, PortFilterCallback portFilterCallback) {
        synchronized (receiveList) {
            if (receiveList != null && !receiveList.isEmpty()) {
                for (int i = receiveList.size() - 1; i >= 0; i--) {
                    byte[] receiveDatas = receiveList.get(i);
                    if (portFilterCallback != null ? portFilterCallback.onFilter(sendDatas, receiveDatas) : true) {//判断指令正确性
                        PortReceiveCache.getInstance().remove(receiveDatas);
                        return receiveDatas;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：remove
     */
    public void remove(byte[] bytes) {
        removeResponse(bytes);
        removeInterrupt(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clear
     */
    public void clear() {
        clearResponse();
        clearInterrupt();
    }
}
