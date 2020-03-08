package com.xc.framework.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author ZhangXuanChen
 * @date 2020/3/1
 * @package com.xc.framework.other
 * @description 自定义Runnable
 */
public abstract class XCRunnable implements Runnable {
    /**
     * @author ZhangXuanChen
     * @date 2020/2/8
     * @description xcHandler
     */
    protected Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            onHandler(msg);
        }
    };

    @Override
    public void run() {
        Object obj = onRun(handler);
        if (obj != null) {
            sendMessage(0x789, obj);
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description sendMessage
     */
    protected void sendMessage(int what) {
        sendMessage(what, null);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description sendMessage
     */
    protected void sendMessage(int what, Object obj) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        if (obj != null) {
            msg.obj = obj;
        }
        handler.sendMessage(msg);
    }

    /**
     * @param
     * @return
     * @author ZhangXuanChen
     * @date 2020/3/3
     * @description run
     */
    protected abstract Object onRun(Handler handler);

    /**
     * @param
     * @return
     * @author ZhangXuanChen
     * @date 2020/3/3
     * @description handler
     */
    protected abstract void onHandler(Message msg);
}
