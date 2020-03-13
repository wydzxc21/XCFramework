package com.xc.framework.socket;

import android.os.Handler;
import android.os.Message;

import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCNetUtil;
import com.xc.framework.util.XCThreadUtil;

import java.net.Socket;

/**
 * @author ZhangXuanChen
 * @date 2020/3/3
 * @package com.hollysys.pcr.ap2310.socket
 * @description 心跳线程
 */
public abstract class SocketHeartbeatThread extends XCThread {
    private final String TAG = "SocketHeartbeatThread";
    private Socket socket;

    public SocketHeartbeatThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected Object onRun(Handler handler) {
        while (isRun()) {
            if (!isSocketHeart() || !isPing()) {
                setRun(false);
                handler.sendEmptyMessage(0x123);
            }
            XCThreadUtil.sleep(1000);
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        if (msg.what == 0x123) {
            onDisconnect(socket);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/4 17:44
     * Description：isSocketHeart
     */
    private boolean isSocketHeart() {
        try {
            socket.sendUrgentData(0xFF);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/4 17:46
     * Description：isPing
     */
    private boolean isPing() {
        try {
            return XCNetUtil.isPing(socket.getInetAddress().getHostAddress());
        } catch (Exception e) {
            return false;
        }
    }

    protected abstract void onDisconnect(Socket socket);
}
