package com.xc.framework.socket.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xc.framework.socket.bean.HandShakeBean;
import com.xc.framework.socket.bean.MsgDataBean;
import com.xc.framework.socket.bean.PulseBean;
import com.xc.framework.socket.client.sdk.OkSocket;
import com.xc.framework.socket.client.sdk.client.ConnectionInfo;
import com.xc.framework.socket.client.sdk.client.OkSocketOptions;
import com.xc.framework.socket.client.sdk.client.action.SocketActionAdapter;
import com.xc.framework.socket.client.sdk.client.connection.IConnectionManager;
import com.xc.framework.socket.constant.MsgConstant;
import com.xc.framework.socket.core.iocore.interfaces.IPulseSendable;
import com.xc.framework.socket.core.iocore.interfaces.ISendable;
import com.xc.framework.socket.core.pojo.OriginalData;

import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Date：2020/3/12
 * Author：ZhangXuanChen
 * Description：socket客户端
 */
public class SocketClientManager {
    private static final String TAG = "SocketClientManager";
    OnSocketClientListener onSocketClientListener;
    IConnectionManager clientManager;

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/14 8:25
     * Description：SocketClientManager
     * Param：serverIp 服务器ip
     * Param：port 端口号（0 - 65535）
     */
    public SocketClientManager(String serverIp, int port) {
        this(serverIp, port, null);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/14 8:26
     * Description：SocketClientManager
     * Param：serverIp 服务器IP
     * Param：port 端口号（0 - 65535）
     * Param：okSocketOptions 配置选项
     */
    public SocketClientManager(String serverIp, int port, OkSocketOptions okSocketOptions) {
        init(serverIp, port, okSocketOptions);
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
                    if (onSocketClientListener != null) {
                        onSocketClientListener.onReceive(ip, data);
                    }
                    break;
                case 0x234:
                    if (onSocketClientListener != null) {
                        onSocketClientListener.onConnect((String) msg.obj);
                    }
                    break;
                case 0x345:
                    if (onSocketClientListener != null) {
                        onSocketClientListener.onDisconnect((String) msg.obj);
                    }
                    break;
            }
        }
    };

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/12 9:01
     * Description：init
     */
    private void init(String serverIp, int port, OkSocketOptions okSocketOptions) {
        if (!TextUtils.isEmpty(serverIp)) {
            clientManager = OkSocket.open(new ConnectionInfo(serverIp, port));
            if (okSocketOptions != null) {
                okSocketOptions.setPulseFrequency(okSocketOptions.getPulseFrequency() > 1000 ? okSocketOptions.getPulseFrequency() : 1000);
                clientManager.option(okSocketOptions);
            } else {
                clientManager.option(new OkSocketOptions.Builder().build());
            }
            clientManager.registerReceiver(new MyClientActionAdapter());
            clientManager.connect();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 15:58
     * Description：start
     */
    public void start() {
        if (clientManager != null) {
            clientManager.connect();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 15:58
     * Description：stop
     */
    public void stop() {
        if (clientManager != null) {
            clientManager.disconnect();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 16:12
     * Description：send
     */
    public void send(String data) {
        if (clientManager != null && !TextUtils.isEmpty(data)) {
            clientManager.send(new MsgDataBean(data));
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 16:00
     * Description：setOnSocketServerListener
     */
    public void setOnSocketClientListener(OnSocketClientListener onSocketClientListener) {
        this.onSocketClientListener = onSocketClientListener;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/11 13:40
     * Description：MyClientActionAdapter
     */
    class MyClientActionAdapter extends SocketActionAdapter {
        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            clientManager.send(new HandShakeBean());
            clientManager.getPulseManager().setPulseSendable(new PulseBean());
            Log.i(TAG, "onSocketConnectionSuccess: 连接成功");
            Message msg = handler.obtainMessage();
            msg.what = 0x234;
            msg.obj = info.getIp();
            handler.sendMessage(msg);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            Message msg = handler.obtainMessage();
            msg.what = 0x345;
            msg.obj = info.getIp();
            handler.sendMessage(msg);
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            try {
                String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
                JSONObject jsonObject = new JSONObject(str);
                int cmd = jsonObject.optInt("cmd");
                switch (cmd) {
                    case MsgConstant.HANDSHAKE:
                        clientManager.getPulseManager().pulse();
                        break;
                    case MsgConstant.PULSE:
                        clientManager.getPulseManager().feed();
                        break;
                    case MsgConstant.MESSAGE:
                        String dataStr = jsonObject.optString("data");
                        Bundle b = new Bundle();
                        b.putString("ip", info.getIp());
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
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
        }
    }
}
