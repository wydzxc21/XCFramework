package com.xc.framework.socket.client.impl.client;


import com.xc.framework.socket.client.impl.client.abilities.IConnectionSwitchListener;
import com.xc.framework.socket.client.sdk.client.ConnectionInfo;
import com.xc.framework.socket.client.sdk.client.OkSocketOptions;
import com.xc.framework.socket.client.sdk.client.connection.IConnectionManager;
import com.xc.framework.socket.common.interfaces.server.IServerManager;
import com.xc.framework.socket.common.interfaces.server.IServerManagerPrivate;
import com.xc.framework.socket.core.utils.SLog;
import com.xc.framework.socket.server.impl.ServerManagerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */
public class ManagerHolder {

    private volatile Map<ConnectionInfo, IConnectionManager> mConnectionManagerMap = new HashMap<>();

    private volatile Map<Integer, IServerManagerPrivate> mServerManagerMap = new HashMap<>();

    private static class InstanceHolder {
        private static final ManagerHolder INSTANCE = new ManagerHolder();
    }

    public static ManagerHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ManagerHolder() {
        mConnectionManagerMap.clear();
    }

    public IServerManager getServer(int localPort) {
        IServerManagerPrivate manager = mServerManagerMap.get(localPort);
        if (manager == null) {
            manager = new ServerManagerImpl();
            if (manager == null) {
                String err = "Oksocket.Server() load error.";
                SLog.e(err);
                throw new IllegalStateException(err);
            } else {
                synchronized (mServerManagerMap) {
                    mServerManagerMap.put(localPort, manager);
                }
                manager.initServerPrivate(localPort);
                return manager;
            }
        }
        return manager;
    }

    public IConnectionManager getConnection(ConnectionInfo info) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager == null) {
            return getConnection(info, OkSocketOptions.getDefault());
        } else {
            return getConnection(info, manager.getOption());
        }
    }

    public IConnectionManager getConnection(ConnectionInfo info, OkSocketOptions okOptions) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager != null) {
            if (!okOptions.isConnectionHolden()) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(info);
                }
                return createNewManagerAndCache(info, okOptions);
            } else {
                manager.option(okOptions);
            }
            return manager;
        } else {
            return createNewManagerAndCache(info, okOptions);
        }
    }

    private IConnectionManager createNewManagerAndCache(ConnectionInfo info, OkSocketOptions okOptions) {
        AbsConnectionManager manager = new ConnectionManagerImpl(info);
        manager.option(okOptions);
        manager.setOnConnectionSwitchListener(new IConnectionSwitchListener() {
            @Override
            public void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo,
                                               ConnectionInfo newInfo) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(oldInfo);
                    mConnectionManagerMap.put(newInfo, manager);
                }
            }
        });
        synchronized (mConnectionManagerMap) {
            mConnectionManagerMap.put(info, manager);
        }
        return manager;
    }

    protected List<IConnectionManager> getList() {
        List<IConnectionManager> list = new ArrayList<>();

        Map<ConnectionInfo, IConnectionManager> map = new HashMap<>(mConnectionManagerMap);
        Iterator<ConnectionInfo> it = map.keySet().iterator();
        while (it.hasNext()) {
            ConnectionInfo info = it.next();
            IConnectionManager manager = map.get(info);
            if (!manager.getOption().isConnectionHolden()) {
                it.remove();
                continue;
            }
            list.add(manager);
        }
        return list;
    }


}
