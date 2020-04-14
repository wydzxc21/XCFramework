package com.xc.framework.socket.common.interfaces.server;


import com.xc.framework.socket.common.interfaces.client.IDisConnectable;
import com.xc.framework.socket.common.interfaces.client.ISender;
import com.xc.framework.socket.core.protocol.IReaderProtocol;

import java.io.Serializable;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getHostIp();

    String getHostName();

    String getUniqueTag();

    void setReaderProtocol(IReaderProtocol protocol);

    void addIOCallback(IClientIOCallback clientIOCallback);

    void removeIOCallback(IClientIOCallback clientIOCallback);

    void removeAllIOCallback();

}
