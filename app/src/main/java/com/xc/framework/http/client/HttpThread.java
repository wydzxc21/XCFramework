package com.xc.framework.http.client;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.http.HttpConnection;
import com.xc.framework.http.HttpParam;
import com.xc.framework.http.interfaces.DownloadCallBack;
import com.xc.framework.http.interfaces.UploadCallBack;
import com.xc.framework.util.XCNetUtil;

/**
 * @author ZhangXuanChen
 * @date 2015-9-15
 * @package com.frame.net
 * @description http请求网络线程类，数据返回通过handler传递
 */
public class HttpThread extends BaseThread {
    public static final String TagName = "HttpThread";
    //
    private Context context;// 上下文
    private HttpParam params;// 参数集,包含请求URL
    private Handler handler;// 消息传递者
    private int requestCode;// 传递标识
    private ConnectionConfig config;// 连接超时时间参数
    private boolean isShowLog;// 是否打印log日志
    private UploadCallBack uploadCallBack;// 上传回调
    private DownloadCallBack downloadCallBack;// 下载回调

    /**
     * 构造函数
     *
     * @param params
     * @param handler
     * @param what
     * @param config
     * @param isShowLog tagName：HttpThread
     */
    public HttpThread(Context context, HttpParam params, Handler handler, int requestCode, ConnectionConfig config, UploadCallBack uploadCallBack, DownloadCallBack downloadCallBack, boolean isShowLog) {
        this.context = context;
        this.params = params;
        this.handler = handler;
        this.requestCode = requestCode;
        this.config = config;
        this.uploadCallBack = uploadCallBack;
        this.downloadCallBack = downloadCallBack;
        this.isShowLog = isShowLog;
        setName(params.getUrlName());// 设线程名为访问的接口名称
    }

    /**
     * run
     */
    @Override
    protected void threadRun() {
        String requestResult = "";
        // 发送结果
        Message msg = new Message();
        msg.what = requestCode;
        if (XCNetUtil.isNetworkUse(context)) {
            requestResult = requestStart();
            msg.obj = requestResult;
            handler.sendMessage(msg);
        } else {
            if (isShowLog) {
                Log.i(TagName, "----------网络不可连接----------");
            }
            msg.obj = requestResult;
            handler.sendMessageDelayed(msg, config.getConnectionTimeout());
        }

        if (isShowLog) {
            Log.i(TagName, "----------请求访问结束----------");
        }
    }

    /**
     * 开始请求
     *
     * @return
     */
    private String requestStart() {
        if (isShowLog) {
            Log.i(TagName, "----------请求访问开始----------");
            params.outURL(TagName);
        }
        //
        if (params.getPostList() != null && !params.getPostList().isEmpty() || params.getContentBodyMap() != null && !params.getContentBodyMap().isEmpty() || params.getJsonMap() != null && !params.getJsonMap().isEmpty()) {// 有post参或有文件上传,有json参上传
            return HttpConnection.httpPost(params, config, uploadCallBack);
        } else {
            return HttpConnection.httpGet(context, params, config, downloadCallBack);
        }
    }
}
