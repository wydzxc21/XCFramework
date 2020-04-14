package com.xc.framework.socket.client.sdk.client.bean;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public interface IPulse {
    /**
     * 开始心跳
     */
    void pulse();

    /**
     * 触发一次心跳
     */
    void trigger();

    /**
     * 停止心跳
     */
    void dead();

    /**
     * 心跳返回后喂狗,ACK
     */
    void feed();
}

