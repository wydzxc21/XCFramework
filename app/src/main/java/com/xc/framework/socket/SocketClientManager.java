package com.xc.framework.socket;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCStringUtil;
import com.xc.framework.util.XCThreadUtil;

import java.net.Socket;

/**
 * @author ZhangXuanChen
 * @date 2020/2/29
 * @package com.zxc.threaddemo.socket
 * @description socket客户端
 */
@SuppressLint("LongLogTag")
public class SocketClientManager {
    public final String TAG = "SocketClientManager";
    public static SocketClientManager mSocketClientManager;
    private Socket socket;
    private ClientThread clientThread;
    private String ip;
    private int port;

    /**
     * @author ZhangXuanChen
     * @date 2020/3/2
     * @package com.xc.framework.socket
     * @description getInstance
     */
    public static SocketClientManager getInstance() {
        if (mSocketClientManager == null) {
            mSocketClientManager = new SocketClientManager();
        }
        return mSocketClientManager;
    }

    /**
     * @param ip   服务端ip地址
     * @param port 0 - 65535
     * @author ZhangXuanChen
     * @date 2020/3/1
     * @description 启动客户端
     */
    public void startClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        if (port < 0 || port > 65535) {
            return;
        }
        if (clientThread != null) {
            return;
        }
        clientThread = new ClientThread(ip, port);
        clientThread.startThread();
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/2/29
     * @description stopClient
     */
    public void stopClient() {
        try {
            XCThreadUtil.getInstance().stopAll();
            if (clientThread != null) {
                clientThread.stopThread();
                clientThread = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/3/1
     * @package com.xc.framework.socket.client
     * @description ClientThread
     */
    class ClientThread extends XCThread {
        String ip;
        int port;

        public ClientThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public Object onRun(Handler handler) {
            return setConnect(ip, port);
        }

        @Override
        public void onHandler(Message msg) {
            Socket socket = (Socket) msg.obj;
            if (socket != null) {
                if (onSocketClientListener != null) {
                    onSocketClientListener.onConnect(socket.getInetAddress().getHostAddress());
                }
                //心跳
                SocketHeartbeatThread heartbeatThread = new SocketHeartbeatThread(socket) {
                    @Override
                    protected void onDisconnect(Socket socket) {
                        if (onSocketClientListener != null) {
                            onSocketClientListener.onDisconnect(socket.getInetAddress().getHostAddress());
                        }
                    }
                };
                heartbeatThread.startThread();
                //接收
                SocketReceivedThread receivedThread = new SocketReceivedThread(socket) {
                    @Override
                    public void onReceive(Socket socket, String data) {
                        if (onSocketClientListener != null) {
                            onSocketClientListener.onReceive(socket.getInetAddress().getHostAddress(), data);
                        }
                    }
                };
                receivedThread.startThread();
            }
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/3
     * @description setConnect
     */
    private Socket setConnect(String ip, int port) {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (clientThread.isRun() && socket == null) {
            try {
                socket = new Socket(ip, port);
                socket.setSoTimeout(5 * 1000);
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
                socket.setReuseAddress(true);
                Log.i(TAG, "onRun: 已连接");
            } catch (Exception e) {
                Log.i(TAG, "onRun: 未连接");
            }
            XCThreadUtil.sleep(1000);
        }
        return socket;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/29
     * @description send
     */
    public void send(String content) {
        if (socket != null && !XCStringUtil.isEmpty(content)) {
            new Thread(new SocketSendRunnable(socket, content)).start();
        }
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置接收监听
     */
    public void setOnSocketClientListener(OnSocketClientListener onSocketClientListener) {
        this.onSocketClientListener = onSocketClientListener;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：接口引用
     */
    OnSocketClientListener onSocketClientListener;

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:06
     * Description：OnSocketServerListener
     */
    public interface OnSocketClientListener {
        void onReceive(String ip, String data);

        void onConnect(String ip);

        void onDisconnect(String ip);
    }
}
