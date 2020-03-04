package com.xc.framework.socket.client;

/**
 * Date：2020/3/4
 * Author：ZhangXuanChen
 * Description：OnSocketClientListener
 */
public interface OnSocketClientListener {
    void onReceive(String ip, String data);

    void onConnect(String ip);

    void onDisconnect(String ip);
}
