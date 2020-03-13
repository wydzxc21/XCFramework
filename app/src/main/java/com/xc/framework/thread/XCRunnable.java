package com.xc.framework.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xc.framework.util.XCAppUtil;

/**
 * @author ZhangXuanChen
 * @date 2020/3/1
 * @package com.xc.framework.other
 * @description 自定义Runnable
 */
public abstract class XCRunnable implements Runnable {
    private String name;

    public XCRunnable() {
        init();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/13 8:44
     * Description：init
     */
    private void init() {
        setName(XCAppUtil.getUUId());
    }

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
     * Author：ZhangXuanChen
     * Time：2020/3/13 8:50
     * Description：getName
     */
    public String getName() {
        return name;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/13 8:50
     * Description：setName
     */
    public void setName(String name) {
        this.name = name;
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
