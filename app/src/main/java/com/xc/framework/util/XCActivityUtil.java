package com.xc.framework.util;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

/**
 * @author ZhangXuanChen
 * @date 2015-11-22
 * @package com.xc.framework.utils
 * @description 应用activity管理工具类
 */
public class XCActivityUtil {
	private static Stack<Activity> activityStack;
	private static XCActivityUtil mAppUtil;

	/**
	 * 获取单例
	 * 
	 * @return XCActivityUtil实例
	 */
	public static XCActivityUtil getInstance() {
		if (mAppUtil == null) {
			mAppUtil = new XCActivityUtil();
		}
		return mAppUtil;
	}

	/**
	 * 获取Activity栈
	 * 
	 * @return Activity栈集合
	 */
	public Stack<Activity> getActivityStack() {
		return activityStack;
	}

	/**
	 * 添加Activity到堆栈
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 * 
	 * @return activity
	 */
	public Activity getActivity() {
		Activity activity = null;
		try {
			activity = activityStack.lastElement();
		} catch (Exception e) {
		}
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		try {
			finishActivity(activityStack.lastElement());
		} catch (Exception e) {
		}
	}

	/**
	 * 结束指定Activity
	 * 
	 * @param activity
	 */
	private void finishActivity(Activity activity) {
		try {
			if (activity != null) {
				activityStack.remove(activity);
				activity.finish();
				activity = null;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 结束指定Activity
	 * 
	 * @param activity
	 *            栈内需结束的activity类
	 */
	public void finishActivity(Class<?> activity) {
		try {
			if (activity != null) {
				finishActivity(new Class<?>[] { activity });
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 结束指定Activity
	 * 
	 * @param activites
	 *            多个栈内需结束的activity类
	 */
	public void finishActivity(Class<?>[] activites) {
		try {
			if (activites != null && activites.length > 0) {
				for (int i = 0; i < activites.length; i++) {
					String activityName = activites[i].getSimpleName();
					for (int j = activityStack.size() - 1; j >= 0; j--) {
						Activity activity = activityStack.get(j);
						String name = activity.getClass().getSimpleName();
						if (name.equals(activityName)) {
							finishActivity(activity);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 保留指定Activity结束其他
	 * 
	 * @param activity
	 *            栈内需保留的activity类
	 */
	public void finishElseActivity(Class<?> activity) {
		try {
			if (activity != null) {
				finishElseActivity(new Class<?>[] { activity });
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 保留指定Activity结束其他
	 * 
	 * @param activites
	 *            多个栈内需保留的activity类
	 */
	public void finishElseActivity(Class<?>[] activites) {
		try {
			if (activites != null && activites.length > 0) {
				for (int i = 0; i < activites.length; i++) {
					String activityName = activites[i].getSimpleName();
					for (int j = activityStack.size() - 1; j >= 0; j--) {
						Activity activity = activityStack.get(j);
						String name = activity.getClass().getSimpleName();
						if (!name.equals(activityName)) {
							finishActivity(activity);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		try {
			for (int i = activityStack.size() - 1; i >= 0; i--) {
				if (null != activityStack.get(i)) {
					finishActivity(activityStack.get(i));
				}
			}
			activityStack.clear();
		} catch (Exception e) {
		}
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
		}
	}

}
