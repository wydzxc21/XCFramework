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
    private final ArrayList<byte[]> responseList;
    private final ArrayList<byte[]> interruptList;
    private static final Object mLock = new Object();
    public static PortReceiveCache mPortReceiveCache;

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：getInstance
     */
    public static PortReceiveCache getInstance() {
        synchronized (mLock) {
            if (mPortReceiveCache == null) {
                mPortReceiveCache = new PortReceiveCache();
            }
        }
        return mPortReceiveCache;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：PortReceiveCache
     */
    public PortReceiveCache() {
        responseList = new ArrayList<byte[]>();
        interruptList = new ArrayList<byte[]>();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResponse
     */
    public void addResponse(byte[] bytes) {
        responseList.add(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:18
     * Description：getResponseList
     */
    public ArrayList<byte[]> getResponseList() {
        return responseList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 12:13
     * Description：removeResponse
     */
    public void removeResponse(byte[] bytes) {
        responseList.remove(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:35
     * Description：clearResponse
     */
    public void clearResponse() {
        responseList.clear();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addInterrupt
     */
    public void addInterrupt(byte[] bytes) {
        interruptList.add(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:19
     * Description：getInterruptList
     */
    public ArrayList<byte[]> getInterruptList() {
        return interruptList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 12:13
     * Description：removeInterrupt
     */
    public void removeInterrupt(byte[] bytes) {
        interruptList.remove(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clearInterrupt
     */
    public void clearInterrupt() {
        interruptList.clear();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:47
     * Description：getReceiveDatas
     */
    public byte[] getReceiveDatas(List<byte[]> receiveList, byte[] sendDatas, PortFilterCallback portFilterCallback) {
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

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：remove
     */

    public void remove(final byte[] bytes) {
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
