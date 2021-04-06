package com.xc.framework.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * @author ZhangXuanChen
 * @date 2015-9-15
 * @package com.frame.net
 * @description 线程管理工具类
 */

public class XCThreadUtil {
    private static LinkedHashMap<String, Thread> threadMap;// 线程集合
    private static XCThreadUtil mThreadManager;

    /**
     * 获取实例
     *
     * @return XCThreadUtil实例
     */
    public static XCThreadUtil getInstance() {
        if (mThreadManager == null) {
            mThreadManager = new XCThreadUtil();
            threadMap = new LinkedHashMap<String, Thread>();
        }
        return mThreadManager;
    }

    /**
     * 添加线程到集合中
     *
     * @param threadName 线程名
     * @param thread     线程
     */
    public void addThreadList(String threadName, Thread thread) {
        if (threadMap != null) {
            threadMap.put(threadName, thread);
        }
    }

    /**
     * 停止某个线程
     *
     * @param threadNameOrUrlName 线程名或url名
     */
    public void stopSingle(String threadNameOrUrlName) {
        try {
            if (threadMap != null && !threadMap.isEmpty()) {
                interruptThread(threadMap.get(threadNameOrUrlName));
                threadMap.remove(threadNameOrUrlName);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 停止所有线程
     */
    public void stopAll() {
        try {
            if (threadMap != null && !threadMap.isEmpty()) {
                for (Thread thread : threadMap.values()) {
                    interruptThread(thread);
                }
                threadMap.clear();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 中断线程
     *
     * @param thread thread对象
     */
    private void interruptThread(Thread thread) {
        try {
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
        } catch (Exception e) {
            Log.e("Thread", "thread.interrupt()异常," + getClass().getName());
        }
    }

    /**
     * 获取正在运行的所有线程
     *
     * @return 线程集合
     */
    public List<Thread> getThreadList() {
        List<Thread> tempList = new ArrayList<Thread>();
        if (threadMap != null && !threadMap.isEmpty()) {
            for (Thread thread : threadMap.values()) {
                tempList.add(thread);
            }
        }
        return tempList;
    }

    /**
     * 返回正在执行的线程数量
     *
     * @return 线程数
     */
    public int getThreadCount() {
        if (threadMap != null) {
            return threadMap.size();
        }
        return 0;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/1
     * @description sleep
     */
    public static void sleep(long millis) {
        sleep(null, millis);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/1
     * @description sleep
     */
    public static void sleep(Thread thread, long millis) {
        if (thread == null) {
            thread = Thread.currentThread();
        }
        if (millis <= 0) {
            return;
        }
        try {
            thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/11 13:50
     * Description：park
     */
    public static void park() {
        park(null, -1);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/11 13:50
     * Description：park
     */
    public static void park(Thread thread) {
        park(thread, -1);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/11 13:50
     * Description：park
     */
    public static void park(long nanos) {
        park(null, nanos);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/11 13:50
     * Description：park
     */
    public static void park(Thread thread, long nanos) {
        if (thread == null) {
            thread = Thread.currentThread();
        }
        if (nanos > 0) {
            LockSupport.parkNanos(thread, nanos);
        } else {
            LockSupport.park(thread);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/11 13:46
     * Description：unpark
     */
    public static void unpark() {
        unpark(null);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/11 13:46
     * Description：unpark
     */
    public static void unpark(Thread thread) {
        if (thread == null) {
            thread = Thread.currentThread();
        }
        LockSupport.unpark(thread);
    }
}
