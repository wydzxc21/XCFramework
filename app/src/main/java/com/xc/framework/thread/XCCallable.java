package com.xc.framework.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.Callable;

/**
 * Date：2020/7/11
 * Author：ZhangXuanChen
 * Description：自定义Callable
 */
public abstract class XCCallable<V> implements Callable<V> {

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

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description sendMessage
     */
    public void sendMessage(int what) {
        sendMessage(what, null);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description sendMessage
     */
    public void sendMessage(int what, Object obj) {
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
     * @description handler
     */
    protected abstract void onHandler(Message msg);
}
