package com.xc.framework.http.interfaces;

/**
 * @author ZhangXuanChen
 * @date 2015-9-15
 * @package com.frame.net
 * @description 线程处理接口
 */
public interface ThreadInterface {
	
	/**
	 * 开启线程.
	 */
	public void startThread();

	/**
	 * 停止当前线程（安全提前退出）.
	 */
	public void stopThread();
}
