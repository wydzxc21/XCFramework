package com.xc.framework.socket.common.interfaces;


import com.xc.framework.socket.core.iocore.interfaces.IIOCoreOptions;
import com.xc.framework.socket.core.iocore.interfaces.ISendable;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public interface IIOManager<E extends IIOCoreOptions> {
    void startEngine();

    void setOkOptions(E options);

    void send(ISendable sendable);

    void close();

    void close(Exception e);

}
