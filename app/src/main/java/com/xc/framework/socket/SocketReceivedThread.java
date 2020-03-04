package com.xc.framework.socket;

import android.os.Handler;
import android.os.Message;

import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCStringUtil;
import com.xc.framework.util.XCThreadUtil;

import java.io.InputStream;
import java.net.Socket;

/**
 * @author ZhangXuanChen
 * @date 2020/2/29
 * @package com.zxc.threaddemo.socket
 * @description socket接收线程
 */
public abstract class SocketReceivedThread extends XCThread {
    public static final String TAG = "SocketReceivedThread";
    private Socket socket;

    public SocketReceivedThread(Socket socket) {
        this.socket = socket;
    }


    @Override
    protected Object onRun(Handler handler) {
        while (isRun) {
            try {
                InputStream is = socket.getInputStream();
                int available = is.available();
                if (available > 0) {
                    byte[] buffer = new byte[available];
                    is.read(buffer);
                    String str = new String(buffer);
                    if (!XCStringUtil.isEmpty(str)) {
                        Message msg = handler.obtainMessage();
                        msg.what = 0x123;
                        msg.obj = str;
                        handler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
            }
            XCThreadUtil.sleep(100);
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        if (msg.what == 0x123) {
            onReceive(socket, (String) msg.obj);
        }
    }


    public abstract void onReceive(Socket socket, String data);


}
