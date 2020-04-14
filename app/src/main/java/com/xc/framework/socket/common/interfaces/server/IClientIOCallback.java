package com.xc.framework.socket.common.interfaces.server;


import com.xc.framework.socket.core.iocore.interfaces.ISendable;
import com.xc.framework.socket.core.pojo.OriginalData;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
