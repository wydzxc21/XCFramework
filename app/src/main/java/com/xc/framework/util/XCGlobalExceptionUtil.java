package com.xc.framework.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author ZhangXuanChen
 * @date 2014-08-04
 * @package com.xc.framework.utils
 * @description 全局异常捕获工具类
 */
public class XCGlobalExceptionUtil implements UncaughtExceptionHandler {
	private static XCGlobalExceptionUtil mGlobalException;
	private UncaughtExceptionHandler mDefaultHandler;
	
	public XCGlobalExceptionUtil() {
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	/**
	 * 获取单例
	 * @return XCGlobalExceptionUtil实例
	 */
	public static XCGlobalExceptionUtil getInstance() {
		if (mGlobalException == null) {
			mGlobalException = new XCGlobalExceptionUtil();
		}
		return mGlobalException;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (mDefaultHandler != null) {
			if (globalException != null) {
				globalException.onGlobalException(ex);
			}
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 错误信息转成String
	 * 
	 * @param ex
	 * @return
	 */
	public String parseThrowableString(Throwable ex) {
		String result = null;
		if (null != ex) {
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			result = writer.toString();
		}
		return result;
	}

	/**
	 * 异常监听
	 * 
	 * @param globalException 实现接口
	 */
	public void setOnGlobalExceptionListener(OnGlobalExceptionListener globalException) {
		this.globalException = globalException;
	}

	// 接口引用
	OnGlobalExceptionListener globalException;

	/**
	 * 异常监听接口
	 * 
	 * @author ZhangXuanChen
	 * @created 2015-1-9
	 */
	public interface OnGlobalExceptionListener {
		void onGlobalException(Throwable ex);
	}
}
