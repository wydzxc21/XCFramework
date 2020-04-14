package com.xc.framework.socket.client.sdk.client;


import java.net.Socket;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public abstract class OkSocketFactory {

    public abstract Socket createSocket(ConnectionInfo info, OkSocketOptions options) throws Exception;

}
