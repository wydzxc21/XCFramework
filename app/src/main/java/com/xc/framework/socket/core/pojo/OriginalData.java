package com.xc.framework.socket.core.pojo;

import java.io.Serializable;


/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket-原始数据结构体
 */
public final class OriginalData implements Serializable {
    /**
     * 原始数据包头字节数组
     */
    private byte[] mHeadBytes;
    /**
     * 原始数据包体字节数组
     */
    private byte[] mBodyBytes;

    public byte[] getHeadBytes() {
        return mHeadBytes;
    }

    public void setHeadBytes(byte[] headBytes) {
        mHeadBytes = headBytes;
    }

    public byte[] getBodyBytes() {
        return mBodyBytes;
    }

    public void setBodyBytes(byte[] bodyBytes) {
        mBodyBytes = bodyBytes;
    }
}
