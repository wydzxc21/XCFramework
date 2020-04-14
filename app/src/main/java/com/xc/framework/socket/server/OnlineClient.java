package com.xc.framework.socket.server;


import com.xc.framework.socket.common.interfaces.server.IClient;

/**
 * Date：2020/3/12
 * Author：ZhangXuanChen
 * Description：
 */
public class OnlineClient {
    String ip;
    IClient iClient;
    long lastPulseTime;

    public OnlineClient() {
    }

    public OnlineClient(String ip, IClient iClient, long lastPulseTime) {
        this.ip = ip;
        this.iClient = iClient;
        this.lastPulseTime = lastPulseTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public IClient getiClient() {
        return iClient;
    }

    public void setiClient(IClient iClient) {
        this.iClient = iClient;
    }

    public long getLastPulseTime() {
        return lastPulseTime;
    }

    public void setLastPulseTime(long lastPulseTime) {
        this.lastPulseTime = lastPulseTime;
    }
}
