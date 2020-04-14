package com.xc.framework.socket.server.action;


import com.xc.framework.socket.common.interfaces.server.IClient;
import com.xc.framework.socket.common.interfaces.server.IClientPool;
import com.xc.framework.socket.common.interfaces.server.IServerActionListener;
import com.xc.framework.socket.common.interfaces.server.IServerShutdown;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public abstract class ServerActionAdapter implements IServerActionListener {
    @Override
    public void onServerListening(int serverPort) {

    }

    @Override
    public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {

    }

    @Override
    public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {

    }

    @Override
    public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {

    }

    @Override
    public void onServerAlreadyShutdown(int serverPort) {

    }
}
