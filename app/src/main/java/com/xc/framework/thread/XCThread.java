package com.xc.framework.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xc.framework.util.XCAppUtil;
import com.xc.framework.util.XCThreadUtil;

/**
 * @author ZhangXuanChen
 * @date 2020/3/1
 * @package com.xc.framework.other
 * @description 自定义线程
 */
public abstract class XCThread extends Thread {
    protected boolean isRun = false;

    public XCThread() {
        init();
    }


    /**
     * @author ZhangXuanChen
     * @date 2020/3/3
     * @description init
     */
    private void init() {
        setName(XCAppUtil.getUUId());
        XCThreadUtil.getInstance().addThreadList(getName(), this);
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
        super.run();
        Object obj = onRun(handler);
        if (obj != null) {
            Message msg = handler.obtainMessage();
            msg.what = 0x789;
            msg.obj = obj;
            handler.sendMessage(msg);
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
        XCThreadUtil.getInstance().stopSingle(getName());
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
