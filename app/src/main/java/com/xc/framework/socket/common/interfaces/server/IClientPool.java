package com.xc.framework.socket.common.interfaces.server;


import com.xc.framework.socket.core.iocore.interfaces.ISendable;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public interface IClientPool<T, K> {

    void cache(T t);

    T findByUniqueTag(K key);

    int size();

    void sendToAll(ISendable sendable);
}
