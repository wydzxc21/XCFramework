package com.xc.framework.socket.core.iocore.interfaces;


import com.xc.framework.socket.core.protocol.IReaderProtocol;

import java.nio.ByteOrder;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public interface IIOCoreOptions {

    ByteOrder getReadByteOrder();

    int getMaxReadDataMB();

    IReaderProtocol getReaderProtocol();

    ByteOrder getWriteByteOrder();

    int getReadPackageBytes();

    int getWritePackageBytes();

    boolean isDebug();

}
