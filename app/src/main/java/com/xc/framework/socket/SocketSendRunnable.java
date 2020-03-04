package com.xc.framework.socket;

import android.os.Handler;
import android.os.Message;

import com.xc.framework.thread.XCRunnable;
import com.xc.framework.util.XCStringUtil;

import java.io.OutputStream;
import java.net.Socket;

/**
 * @author ZhangXuanChen
 * @date 2020/2/29
 * @package com.zxc.threaddemo.socket
 * @description socket发送线程
 */
public class SocketSendRunnable extends XCRunnable {
    public final String TAG = "SocketSendRunnable";
    private Socket socket;
    private String content;

    public SocketSendRunnable(Socket socket, String content) {
        this.socket = socket;
        this.content = content;
    }


    @Override
    public Object onRun(Handler handler) {
        try {
            if (socket != null && !XCStringUtil.isEmpty(content)) {
                OutputStream os = socket.getOutputStream();
                os.write((content).getBytes("utf-8"));
                os.flush();
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {

    }
}
