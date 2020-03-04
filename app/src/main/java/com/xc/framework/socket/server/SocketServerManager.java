package com.xc.framework.socket.server;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.socket.SocketHeartbeatThread;
import com.xc.framework.socket.SocketReceivedThread;
import com.xc.framework.socket.SocketSendRunnable;
import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCStringUtil;
import com.xc.framework.util.XCThreadUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author ZhangXuanChen
 * @date 2020/2/29
 * @package com.zxc.threaddemo.socket
 * @description socket服务端
 */
public class SocketServerManager {
    public final String TAG = "SocketServerManager";
    public static SocketServerManager mSocketServerManager;
    private ServerSocket serverSocket;
    private XCThread serverThread;
    private LinkedList<Socket> clientList;
    private OnSocketServerListener onSocketServerListener;

    /**
     * @author ZhangXuanChen
     * @date 2020/3/2
     * @package com.xc.framework.socket
     * @description getInstance
     */
    public static SocketServerManager getInstance() {
        if (mSocketServerManager == null) {
            mSocketServerManager = new SocketServerManager();
        }
        return mSocketServerManager;
    }


    public SocketServerManager() {
        clientList = new LinkedList<Socket>();
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/2
     * @description getClientList
     */
    public LinkedList<Socket> getClientList() {
        return clientList;
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/11/26 14:07
     * Description：设置接收监听
     */
    public void setOnSocketServerListener(OnSocketServerListener onSocketServerListener) {
        this.onSocketServerListener = onSocketServerListener;
    }

    /**
     * @param port 0 - 65535
     * @author ZhangXuanChen
     * @date 2020/2/29
     * @description startServer
     */
    public void startServer(int port) {
        if (port < 0 || port > 65535) {
            return;
        }
        serverThread = new ServerThread(port);
        serverThread.startThread();
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/2
     * @description 断开连接
     */
    public void stopServer() {
        try {
            XCThreadUtil.getInstance().stopAll();
            if (serverThread != null) {
                serverThread.stopThread();
            }
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            if (onSocketServerListener != null) {
                onSocketServerListener = null;
            }
        } catch (Exception e) {
        }
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/3/1
     * @package com.xc.framework.socket.server
     * @description ServerThread
     */
    class ServerThread extends XCThread {
        int port;

        public ServerThread(int port) {
            this.port = port;
        }

        @Override
        public Object onRun(Handler handler) {
            try {
                serverSocket = new ServerSocket(port);
                while (serverThread.isRun() && serverSocket != null) {
                    Socket socket = serverSocket.accept();
                    addSocketList(socket, clientList);
                    Message msg = handler.obtainMessage();
                    msg.what = 0x321;
                    msg.obj = socket;
                    handler.sendMessage(msg);
                    XCThreadUtil.sleep(1000);
                }
            } catch (Exception e) {
                Log.i(TAG, "onRun: " + e.getMessage());
            }
            return null;
        }

        @Override
        public void onHandler(Message msg) {
            Socket socket = (Socket) msg.obj;
            if (socket != null) {
                if (onSocketServerListener != null) {
                    onSocketServerListener.onConnect(socket.getInetAddress().getHostAddress());
                }
                //心跳
                SocketHeartbeatThread heartbeatThread = new SocketHeartbeatThread(socket) {
                    @Override
                    protected void onDisconnect(Socket socket) {
                        if (onSocketServerListener != null) {
                            onSocketServerListener.onDisconnect(socket.getInetAddress().getHostAddress());
                        }
                    }
                };
                heartbeatThread.startThread();
                //接收
                SocketReceivedThread receivedThread = new SocketReceivedThread(socket) {
                    @Override
                    public void onReceive(Socket socket, String data) {
                        if (onSocketServerListener != null) {
                            onSocketServerListener.onReceive(socket.getInetAddress().getHostAddress(), data);
                        }
                    }
                };
                receivedThread.startThread();
            }
        }
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/2/29
     * @description send
     */
    public void send(String ip, String content) {
        if (!XCStringUtil.isEmpty(ip) && !XCStringUtil.isEmpty(content)) {
            Socket socket = getSocketById(ip);
            if (socket != null) {
                new Thread(new SocketSendRunnable(socket, content)).start();
            }
        }
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/2/29
     * @description getSocketById
     */
    private Socket getSocketById(String ip) {
        if (!XCStringUtil.isEmpty(ip) && clientList != null && !clientList.isEmpty()) {
            for (int i = clientList.size() - 1; i >= 0; i--) {
                Socket socket = clientList.get(i);
                if (ip.equals(socket.getInetAddress().getHostAddress())) {
                    return socket;
                }
            }
        }
        return null;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/3
     * @description addSocketList
     */
    private void addSocketList(Socket socket, LinkedList<Socket> clientList) {
        if (socket == null) return;
        if (clientList != null && !clientList.isEmpty()) {
            for (int i = clientList.size() - 1; i >= 0; i--) {
                Socket info = clientList.get(i);
                if (socket.getInetAddress().getHostAddress().equals(info.getInetAddress().getHostAddress())) {
                    clientList.remove(info);
                }
            }
        }
        clientList.add(socket);
    }
}
