package com.xc.framework.socket.common.interfaces.server;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public interface IServerActionListener {
    void onServerListening(int serverPort);

    void onClientConnected(IClient client, int serverPort, IClientPool clientPool);

    void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool);

    void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable);

    void onServerAlreadyShutdown(int serverPort);

}
