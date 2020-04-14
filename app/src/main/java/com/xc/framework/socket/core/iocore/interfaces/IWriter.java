package com.xc.framework.socket.core.iocore.interfaces;


import java.io.OutputStream;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public interface IWriter<T extends IIOCoreOptions> {

    void initialize(OutputStream outputStream, IStateSender stateSender);

    boolean write() throws RuntimeException;

    void setOption(T option);

    void offer(ISendable sendable);

    void close();

}
