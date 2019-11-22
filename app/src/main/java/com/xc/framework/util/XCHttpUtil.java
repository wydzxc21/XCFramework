package com.xc.framework.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xc.framework.http.HttpParam;
import com.xc.framework.http.client.ConnectionConfig;
import com.xc.framework.http.client.HttpThread;
import com.xc.framework.http.interfaces.DownloadCallBack;
import com.xc.framework.http.interfaces.RequestCallback;
import com.xc.framework.http.interfaces.UploadCallBack;

/**
 * @author ZhangXuanChen
 * @date 2015-9-16
 * @package com.frame.utils
 * @description 网络请求工具类
 */
public class XCHttpUtil {
	/**
	 * 通过URL方式请求数据,根据不同的参数类型发送请求get或post(同时请求get和post参时采用post请求,get参拼在url后)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param what 常量
	 * @param requestCallback 请求回调
	 * @Description ThreadManager提供线程终止
	 */
	public static void sendRequest(Context context, HttpParam param, int what, final RequestCallback requestCallback) {
		sendRequest(context, param, what, requestCallback, new ConnectionConfig(), false);
	}

	/**
	 * 通过URL方式请求数据,根据不同的参数类型发送请求get或post(同时请求get和post参时采用post请求,get参拼在url后)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param what 常量
	 * @param requestCallback 请求回调
	 * @param isShowLog 是否打印Log tagName：HttpThread
	 * @Description ThreadManager提供线程终止
	 */
	public static void sendRequest(Context context, HttpParam param, int what, final RequestCallback requestCallback, boolean isShowLog) {
		sendRequest(context, param, what, requestCallback, new ConnectionConfig(), isShowLog);
	}

	/**
	 * 通过URL方式请求数据,根据不同的参数类型发送请求get或post(同时请求get和post参时采用post请求,get参拼在url后)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param what 常量
	 * @param requestCallback 请求回调
	 * @param config 连接配置
	 * @param isShowLog 是否打印Log tagName：HttpThread
	 * @Description ThreadManager提供线程终止
	 */
	public static void sendRequest(final Context context, HttpParam param, final int what, final RequestCallback requestCallback, ConnectionConfig config, boolean isShowLog) {
		Handler handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (requestCallback != null) {
					requestCallback.onResult(msg.what, (String) msg.obj);
				}
			}
		};
		new HttpThread(context, param, handler, what, config, null, null, isShowLog).startThread();
	}

	// -----------------------------------------------上传-----------------------------------------------
	/**
	 * 通过post上传文件(支持多个同时上传)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param uploadCallBack 上传回调
	 * @Description ThreadManager提供线程终止
	 */
	public static void uploadFile(Context context, HttpParam param, final UploadCallBack uploadCallBack) {
		uploadFile(context, param, uploadCallBack, new ConnectionConfig(), false);
	}

	/**
	 * 通过post上传文件(支持多个同时上传)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param uploadCallBack 上传回调
	 * @param isShowLog 是否打印Log tagName：HttpThread
	 * @Description ThreadManager提供线程终止
	 */
	public static void uploadFile(Context context, HttpParam param, final UploadCallBack uploadCallBack, boolean isShowLog) {
		uploadFile(context, param, uploadCallBack, new ConnectionConfig(), isShowLog);
	}

	/**
	 * 通过post上传文件(支持多个同时上传)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param uploadCallBack 上传回调
	 * @param config 连接配置
	 * @param isShowLog 是否打印Log tagName：HttpThread
	 * @Description ThreadManager提供线程终止
	 */
	public static void uploadFile(Context context, HttpParam param, final UploadCallBack uploadCallBack, ConnectionConfig config, boolean isShowLog) {
		Handler handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (uploadCallBack != null) {
					uploadCallBack.onResult((String) msg.obj);
				}
			}
		};
		if (param != null) {
			param.clearDownloadParams();
			new HttpThread(context, param, handler, 0x123, config, uploadCallBack, null, isShowLog).startThread();
		}
	}

	// ---------------------------------------------下载----------------------------------------------------------
	/**
	 * 通过get下载文件(只支持单个下载)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param downloadCallBack 下载回调
	 * @Description ThreadManager提供线程终止
	 */
	public static void downloadFile(Context context, HttpParam param, final DownloadCallBack downloadCallBack) {
		downloadFile(context, param, downloadCallBack, new ConnectionConfig(), false);
	}

	/**
	 * 通过get下载文件(只支持单个下载)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param downloadCallBack 下载回调
	 * @param isShowLog 是否打印Log tagName：HttpThread
	 * @Description ThreadManager提供线程终止
	 */
	public static void downloadFile(Context context, HttpParam param, final DownloadCallBack downloadCallBack, boolean isShowLog) {
		downloadFile(context, param, downloadCallBack, new ConnectionConfig(), isShowLog);
	}

	/**
	 * 通过get下载文件(只支持单个下载)
	 * 
	 * @param context 上下文
	 * @param param 参数
	 * @param downloadCallBack 下载回调
	 * @param config 连接配置
	 * @param isShowLog 是否打印Log tagName：HttpThread
	 * @Description ThreadManager提供线程终止
	 */
	public static void downloadFile(Context context, HttpParam param, final DownloadCallBack downloadCallBack, ConnectionConfig config, boolean isShowLog) {
		if (param != null) {
			param.clearUploadParams();
			new HttpThread(context, param, new Handler(), 0x123, config, null, downloadCallBack, isShowLog).startThread();
		}
	}
}
