package com.xc.framework.socket.server.impl;


import com.xc.framework.socket.common.interfaces.dispatcher.IRegister;
import com.xc.framework.socket.common.interfaces.server.IServerActionListener;
import com.xc.framework.socket.common.interfaces.server.IServerManager;
import com.xc.framework.socket.core.iocore.interfaces.IStateSender;
import com.xc.framework.socket.server.action.ServerActionDispatcher;

import java.io.Serializable;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public class AbsServerRegisterProxy implements IRegister<IServerActionListener, IServerManager>, IStateSender {

    protected ServerActionDispatcher mServerActionDispatcher;

    private IServerManager<OkServerOptions> mManager;

    protected void init(IServerManager<OkServerOptions> serverManager) {
        mManager = serverManager;
        mServerActionDispatcher = new ServerActionDispatcher(mManager);
    }

    @Override
    public IServerManager<OkServerOptions> registerReceiver(IServerActionListener socketActionListener) {
        return mServerActionDispatcher.registerReceiver(socketActionListener);
    }

    @Override
    public IServerManager<OkServerOptions> unRegisterReceiver(IServerActionListener socketActionListener) {
        return mServerActionDispatcher.unRegisterReceiver(socketActionListener);
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        mServerActionDispatcher.sendBroadcast(action, serializable);
    }

    @Override
    public void sendBroadcast(String action) {
        mServerActionDispatcher.sendBroadcast(action);
    }
}
