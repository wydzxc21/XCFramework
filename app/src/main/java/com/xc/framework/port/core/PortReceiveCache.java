package com.xc.framework.port.core;

import android.util.Log;

import com.xc.framework.util.XCByteUtil;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Date：2021/3/26
 * Author：ZhangXuanChen
 * Description：串口接收缓存
 */
public class PortReceiveCache {
    private final String TAG = "PortReceiveCache";
    private final CopyOnWriteArrayList<byte[]> responseList;
    private final CopyOnWriteArrayList<byte[]> resultList;

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：PortReceiveCache
     */
    public PortReceiveCache() {
        responseList = new CopyOnWriteArrayList<byte[]>();
        resultList = new CopyOnWriteArrayList<byte[]>();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResponse
     */
    public synchronized boolean addResponse(byte[] bytes) {
        return responseList.add(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:35
     * Description：removeResponse
     */
    public synchronized boolean removeResponse(byte[] bytes) {
        return responseList.remove(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:18
     * Description：getResponseList
     */
    public synchronized CopyOnWriteArrayList<byte[]> getResponseList() {
        return responseList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:35
     * Description：clearResponseList
     */
    public synchronized void clearResponseList() {
        responseList.clear();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResult
     */
    public synchronized boolean addResult(byte[] bytes) {
        return resultList.add(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResult
     */
    public synchronized boolean removeResult(byte[] bytes) {
        return resultList.remove(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:19
     * Description：getResultList
     */
    public synchronized CopyOnWriteArrayList<byte[]> getResultList() {
        return resultList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clearResultList
     */
    public synchronized void clearResultList() {
        resultList.clear();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clear
     */
    public synchronized void clear() {
        clearResponseList();
        clearResultList();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:47
     * Description：remove
     */
    public synchronized boolean remove(byte[] bytes, PortReceiveType receiveType) {
        boolean isSucceed = false;
        if (bytes != null && bytes.length > 0) {
            if (receiveType == PortReceiveType.Response) {//响应
                isSucceed = removeResponse(bytes);
            } else if (receiveType == PortReceiveType.Result) {//结果
                isSucceed = removeResult(bytes);
            }
        }
        return isSucceed;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/7/23
     * @package com.xc.framework.port.core
     * @description getReceiveDatas
     */
    public synchronized byte[] getReceiveDatas(PortReceiveType receiveType, byte[] sendDatas, PortFilterCallback portFilterCallback) {
        CopyOnWriteArrayList<byte[]> receiveList = null;
        if (receiveType == PortReceiveType.Response) {//响应
            receiveList = getResponseList();
        } else if (receiveType == PortReceiveType.Result) {//结果
            receiveList = getResultList();
        }
        if (receiveList != null && !receiveList.isEmpty()) {
            for (byte[] receiveDatas : receiveList) {
                if (portFilterCallback != null ? portFilterCallback.onFilter(sendDatas, receiveDatas, receiveType) : true) {//判断指令正确性
                    Log.i(TAG, "指令-匹配接收:[" + XCByteUtil.toHexStr(sendDatas, true) + " , " + XCByteUtil.toHexStr(receiveDatas, true) + "]");
                    remove(receiveDatas, receiveType);
                    return receiveDatas;
                }
            }
        }
        return null;
    }

}
