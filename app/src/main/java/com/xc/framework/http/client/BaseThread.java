package com.xc.framework.http.client;

import com.xc.framework.http.interfaces.ThreadInterface;
import com.xc.framework.util.XCThreadUtil;

/**
 * @author ZhangXuanChen
 * @date 2015-9-15
 * @package com.frame.net
 * @description 自定义线程基类，实现提前退出线程(在耗时操作为完成前截断)
 */
public abstract class BaseThread extends Thread implements ThreadInterface {
	protected boolean isRun = false;// 线程是否在运行
	protected XCThreadUtil mThreadManager;// 线程管理类

	protected BaseThread() {
		mThreadManager = XCThreadUtil.getInstance();
	}

	@Override
	public void run() {
		isRun = true;
		addThreadList();
		threadRun();
		stopThread();
		isRun = false;
	}

	/**
	 * 子类必须实现的线程执行函数.
	 */
	protected abstract void threadRun();

	/**
	 * 添加当前线程到线程集合中
	 */
	protected void addThreadList() {
		if (mThreadManager != null) {
			mThreadManager.addThreadList(getName(), this);
		}
	}

	/**
	 * 开始线程
	 */
	@Override
	public void startThread() {
		start();
	}

	/**
	 * 停止线程
	 */
	@Override
	public void stopThread() {
		isRun = false;
		if (mThreadManager != null) {
			mThreadManager.stopSingle(getName());
		}
	}

	/**
	 * 线程是否在运行中
	 * 
	 * @return
	 */
	public boolean isRun() {
		return isRun;
	}
}
