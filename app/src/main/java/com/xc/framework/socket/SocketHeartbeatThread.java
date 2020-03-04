package com.xc.framework.socket;

import android.os.Handler;
import android.os.Message;

import com.xc.framework.thread.XCThread;

import java.net.Socket;

/**
 * @author ZhangXuanChen
 * @date 2020/3/3
 * @package com.hollysys.pcr.ap2310.socket
 * @description 心跳线程
 */
public abstract class SocketHeartbeatThread extends XCThread {
    private Socket socket;

    public SocketHeartbeatThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected Object onRun(Handler handler) {
        try {
            while (isRun()) {
                socket.sendUrgentData(0xFF);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            handler.sendEmptyMessage(0x123);
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        if (msg.what == 0x123) {
            onDisconnect(socket);
        }
    }

    protected abstract void onDisconnect(Socket socket);
}
