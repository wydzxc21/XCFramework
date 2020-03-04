package com.xc.framework.socket.server;

/**
 * Date：2020/3/4
 * Author：ZhangXuanChen
 * Description：OnSocketServerListener
 */
public interface OnSocketServerListener {
    void onReceive(String ip, String data);

    void onConnect(String ip);

    void onDisconnect(String ip);
}
