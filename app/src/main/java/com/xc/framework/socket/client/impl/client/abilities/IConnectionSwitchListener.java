package com.xc.framework.socket.client.impl.client.abilities;


import com.xc.framework.socket.client.sdk.client.ConnectionInfo;
import com.xc.framework.socket.client.sdk.client.connection.IConnectionManager;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public interface IConnectionSwitchListener {
    void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo, ConnectionInfo newInfo);
}
