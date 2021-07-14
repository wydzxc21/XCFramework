package com.xc.framework.port.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Date：2021/3/26
 * Author：ZhangXuanChen
 * Description：串口接收缓存
 */
public class PortReceiveCache {
    private final String TAG = "PortReceiveCache";
    private final List<byte[]> responseList;
    private final List<byte[]> resultList;
    public static PortReceiveCache mPortReceiveCache;

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：getInstance
     */
    public static PortReceiveCache getInstance() {
        if (mPortReceiveCache == null) {
            mPortReceiveCache = new PortReceiveCache();
        }
        return mPortReceiveCache;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：PortReceiveCache
     */
    public PortReceiveCache() {
        responseList = Collections.synchronizedList(new ArrayList<byte[]>());
        resultList = Collections.synchronizedList(new ArrayList<byte[]>());
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
    public List<byte[]> getResponseList() {
        return responseList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:35
     * Description：clearResponse
     */
    public void clearResponseList() {
        responseList.clear();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResult
     */
    public void addResult(byte[] bytes) {
        resultList.add(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:19
     * Description：getResultList
     */
    public List<byte[]> getResultList() {
        return resultList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clearResultList
     */
    public void clearResultList() {
        resultList.clear();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:47
     * Description：getReceiveDatas
     */
    public synchronized byte[] getReceiveDatas(List<byte[]> receiveList, byte[] sendDatas, PortFilterCallback portFilterCallback) {
        synchronized (receiveList) {
            if (receiveList != null && !receiveList.isEmpty()) {
                Iterator<byte[]> iterator = receiveList.iterator();
                while (iterator.hasNext()) {
                    byte[] receiveDatas = iterator.next();
                    if (portFilterCallback != null ? portFilterCallback.onFilter(sendDatas, receiveDatas) : true) {//判断指令正确性
                        iterator.remove();
                        return receiveDatas;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clear
     */
    public void clear() {
        clearResponseList();
        clearResultList();
    }
}
