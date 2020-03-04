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
            Message msg = handler.obtainMessage();
            msg.what = 0x789;
            msg.obj = onRun(handler);
            handler.sendMessage(msg);
        }
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
