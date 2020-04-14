package com.xc.framework.socket.common.interfaces.server;


import com.xc.framework.socket.core.iocore.interfaces.IIOCoreOptions;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(int serverPort);
}
