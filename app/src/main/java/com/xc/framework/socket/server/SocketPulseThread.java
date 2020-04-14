package com.xc.framework.socket.server;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


/**
 * @author ZhangXuanChen
 * @date 2020/3/3
 * @package com.hollysys.pcr.ap2310.socket
 * @description 心跳线程
 */
public abstract class SocketPulseThread extends Thread {
    private final String TAG = "SocketHeartbeatThread";
    private boolean isRun = false;
    private long pulse;

    public SocketPulseThread(long pulse) {
        this.pulse = pulse;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description handler
     */
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            onPulse();
        }
    };

    @Override
    public void run() {
        super.run();
        try {
            while (isRun()) {
                Thread.sleep(pulse);
                handler.sendEmptyMessage(0x123);
            }
        } catch (Exception e) {
            isRun = false;
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/1
     * @description isRun
     */
    public boolean isRun() {
        return isRun;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/1
     * @description startThread
     */
    public void startThread() {
        isRun = true;
        start();
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/1
     * @description stopThread
     */
    public void stopThread() {
        isRun = false;
        interrupt();
    }

    protected abstract void onPulse();
}
