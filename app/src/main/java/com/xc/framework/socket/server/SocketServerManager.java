package com.xc.framework.socket.server;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xc.framework.socket.bean.HandShakeBean;
import com.xc.framework.socket.bean.MsgDataBean;
import com.xc.framework.socket.bean.PulseBean;
import com.xc.framework.socket.client.sdk.OkSocket;
import com.xc.framework.socket.common.interfaces.server.IClient;
import com.xc.framework.socket.common.interfaces.server.IClientIOCallback;
import com.xc.framework.socket.common.interfaces.server.IClientPool;
import com.xc.framework.socket.common.interfaces.server.IServerManager;
import com.xc.framework.socket.common.interfaces.server.IServerShutdown;
import com.xc.framework.socket.common.utils.TextUtils;
import com.xc.framework.socket.constant.MsgConstant;
import com.xc.framework.socket.core.iocore.interfaces.ISendable;
import com.xc.framework.socket.core.pojo.OriginalData;
import com.xc.framework.socket.server.action.ServerActionAdapter;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Date：2020/3/11
 * Author：ZhangXuanChen
 * Description：socket服务端
 */
public class SocketServerManager {
    private static final String TAG = "SocketServerManager";
    IServerManager serverManager;
    OnSocketServerListener onSocketServerListener;
    LinkedHashMap<String, OnlineClient> onlineMap;
    SocketPulseThread mSocketPulseThread;
    long pulseFrequency;//心跳频率（毫秒）

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/14 8:26
     * Description：SocketServerManager
     * Param：port 端口号（0 - 65535）
     */
    public SocketServerManager(int port) {
        this(port, 10 * 1000);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/14 8:26
     * Description：SocketServerManager
     * Param：port 端口号（0 - 65535）
     * Param：pulseFrequency 心跳频率（毫秒）
     */
    public SocketServerManager(int port, long pulseFrequency) {
        init(port, pulseFrequency > 2000 ? pulseFrequency : 2000);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/12 8:42
     * Description：handler
     */
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    Bundle b = (Bundle) msg.obj;
                    String ip = b.getString("ip");
                    String data = b.getString("data");
                    if (onSocketServerListener != null) {
                        onSocketServerListener.onReceive(ip, data);
                    }
                    break;
                case 0x234:
                    if (onSocketServerListener != null) {
                        onSocketServerListener.onConnect((String) msg.obj);
                    }
                    break;
                case 0x345:
                    if (onSocketServerListener != null) {
                        onSocketServerListener.onDisconnect((String) msg.obj);
                    }
                    break;
            }
        }
    };

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 15:57
     * Description：init
     */
    private void init(int port, long pulseFrequency) {
        this.pulseFrequency = pulseFrequency;
        onlineMap = new LinkedHashMap<String, OnlineClient>();
        serverManager = OkSocket.server(port).registerReceiver(new MyServerActionAdapter());
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 15:58
     * Description：start
     */
    public void start() {
        if (serverManager != null) {
            serverManager.listen();
            startPulseThread(pulseFrequency);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/12 8:11
     * Description：startPulseThread
     */
    private void startPulseThread(long pulseFrequency) {
        mSocketPulseThread = new SocketPulseThread(pulseFrequency) {
            @Override
            protected void onPulse() {
                judgePulseTime();
            }
        };
        mSocketPulseThread.startThread();
    }


    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 15:58
     * Description：stop
     */
    public void stop() {
        if (serverManager != null) {
            serverManager.shutdown();
        }
        if (onlineMap != null) {
            onlineMap.clear();
        }
        if (mSocketPulseThread != null) {
            mSocketPulseThread.stopThread();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 16:12
     * Description：send
     */
    public void send(String ip, String data) {
        if (onlineMap != null && !onlineMap.isEmpty() && !TextUtils.isEmpty(ip) && !TextUtils.isEmpty(data)) {
            OnlineClient onlineClient = onlineMap.get(ip);
            if (onlineClient != null) {
                IClient iClient = onlineClient.getiClient();
                if (iClient != null) {
                    iClient.send(new MsgDataBean(data));
                }
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 16:00
     * Description：setOnSocketServerListener
     */
    public void setOnSocketServerListener(OnSocketServerListener onSocketServerListener) {
        this.onSocketServerListener = onSocketServerListener;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 13:32
     * Description：MyServerActionAdapter
     */
    class MyServerActionAdapter extends ServerActionAdapter implements IClientIOCallback {
        @Override
        public void onServerListening(int serverPort) {
        }

        @Override
        public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {
            onlineMap.put(client.getHostIp(), new OnlineClient(client.getHostIp(), client, System.currentTimeMillis()));
            Message msg = handler.obtainMessage();
            msg.what = 0x234;
            msg.obj = client.getHostIp();
            handler.sendMessage(msg);
            client.addIOCallback(this);
        }

        @Override
        public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {
            onlineMap.remove(client.getHostIp());
            Message msg = handler.obtainMessage();
            msg.what = 0x345;
            msg.obj = client.getHostIp();
            handler.sendMessage(msg);
            client.removeIOCallback(this);
        }

        @Override
        public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {
            shutdown.shutdown();
        }

        @Override
        public void onServerAlreadyShutdown(int serverPort) {
            serverManager.shutdown();
        }

        @Override
        public void onClientRead(OriginalData data, IClient iClient, IClientPool<IClient, String> iClientPool) {
            try {
                updatePulseTime(iClient.getHostIp());
                String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
                JSONObject jsonObject = new JSONObject(str);
                int cmd = jsonObject.optInt("cmd");
                switch (cmd) {
                    case MsgConstant.HANDSHAKE:
                        iClient.send(new HandShakeBean());
                        break;
                    case MsgConstant.PULSE:
                        iClient.send(new PulseBean());
                        break;
                    case MsgConstant.MESSAGE:
                        String dataStr = jsonObject.optString("data");
                        Bundle b = new Bundle();
                        b.putString("ip", iClient.getHostIp());
                        b.putString("data", dataStr);
                        Message msg = handler.obtainMessage();
                        msg.what = 0x123;
                        msg.obj = b;
                        handler.sendMessage(msg);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClientWrite(ISendable iSendable, IClient iClient, IClientPool<IClient, String> iClientPool) {
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/12 8:19
     * Description：judgePulseTime
     */
    private synchronized void judgePulseTime() {
        if (onlineMap != null && !onlineMap.isEmpty()) {
            for (Iterator<Map.Entry<String, OnlineClient>> it = onlineMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, OnlineClient> item = it.next();
                if (item != null) {
                    String key = item.getKey();
                    OnlineClient value = item.getValue();
                    if (!TextUtils.isEmpty(key) && value != null) {
                        long time = System.currentTimeMillis() - value.getLastPulseTime();
                        if (time > pulseFrequency) {
                            it.remove();
                            Message msg = handler.obtainMessage();
                            msg.what = 0x345;
                            msg.obj = key;
                            handler.sendMessage(msg);
                        }
                    }
                }
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/12 8:16
     * Description：updatePulseTime
     */
    private synchronized void updatePulseTime(String ip) {
        if (!TextUtils.isEmpty(ip)) {
            if (onlineMap != null && !onlineMap.isEmpty()) {
                OnlineClient onlineClient = onlineMap.get(ip);
                if (onlineClient != null) {
                    onlineClient.setLastPulseTime(System.currentTimeMillis());
                }
            }
        }
    }
}
