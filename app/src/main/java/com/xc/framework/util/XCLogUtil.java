package com.xc.framework.util;

import android.content.Context;
import android.util.Log;

import androidx.multidex.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author ZhangXuanChen
 * @date 2015-11-20
 * @package com.xc.framework.utils
 * @description Log日志工具类
 */
public class XCLogUtil {
    private static String FOLDER_NAME = "xcFramework";//文件夹名称
    private static final boolean IS_LOG = BuildConfig.DEBUG;

    /**
     * Log.i
     *
     * @param log 打印信息
     * @deprecated tag为当前类名
     */
    public static void i(Context context, String log) {
        i(context.getClass().getSimpleName(), log);
    }

    /**
     * Log.i
     *
     * @param tag 打印tag
     * @param log 打印信息
     */
    public static void i(String tag, String log) {
        try {
            if (IS_LOG) {
                Log.i("" + tag, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Log.d
     *
     * @param log 打印信息
     * @deprecated tag为当前类名
     */
    public static void d(Context context, String log) {
        d(context.getClass().getSimpleName(), log);
    }

    /**
     * Log.d
     *
     * @param tag 打印tag
     * @param log 打印信息
     */
    public static void d(String tag, String log) {
        try {
            if (IS_LOG) {
                Log.d("" + tag, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Log.e
     *
     * @param log 打印信息
     * @deprecated tag为当前类名
     */
    public static void e(Context context, String log) {
        e(context.getClass().getSimpleName(), log);
    }

    /**
     * Log.e
     *
     * @param tag 打印tag
     * @param log 打印信息
     */
    public static void e(String tag, String log) {
        try {
            if (IS_LOG) {
                Log.e("" + tag, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------------------------------

    /**
     * @param log 保存内容
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 保存log到缓存目录下txt文件（例：2020-02-20.txt）
     */
    public static boolean writeLog(Context context, String log) {
        return writeLog(context, log, null);
    }

    /**
     * @param log      保存内容
     * @param filePath 文件绝对路径（含后缀名）
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 保存log到指定txt文件
     */
    public static boolean writeLog(String log, String filePath) {
        return writeLog(null, log, filePath);
    }

    /**
     * @param log      保存内容
     * @param filePath 文件绝对路径（含后缀名）
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 保存log
     */
    private synchronized static boolean writeLog(Context context, String log, String filePath) {
        if (context != null && XCStringUtil.isEmpty(filePath)) {
            String folderPath = XCFileUtil.getExternalCacheDir(context) + File.separator + FOLDER_NAME;
            String fileName = XCTimeUtil.getCurrentTime(XCTimeUtil.FORMAT_DATE) + ".txt";
            filePath = folderPath + File.separator + fileName;
            //
            if (!XCFileUtil.isFileExist(folderPath)) {
                XCFileUtil.createFolder(folderPath);
            }
            if (!XCFileUtil.isFileExist(filePath)) {
                XCFileUtil.createFile(filePath);
            }
        }
        return XCFileUtil.writeText(log, filePath);
    }

    /**
     * @param
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 读取log
     */
    public synchronized static String readLog(String filePath) {
        return XCFileUtil.readText(filePath);
    }

    /**
     * @param
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 读取log
     */
    public synchronized static String readLog(File file) {
        return XCFileUtil.readText(file);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 获取文件list
     */
    public static List<File> getFileList(Context context) {
        return getFileList(context, null);
    }

    /**
     * @param folderPath 文件夹地址
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 获取文件list
     */
    public static List<File> getFileList(String folderPath) {
        return getFileList(null, folderPath);
    }

    /**
     * @param folderPath 文件夹地址
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 获取文件list
     */
    private static List<File> getFileList(Context context, String folderPath) {
        if (context != null && XCStringUtil.isEmpty(folderPath)) {
            folderPath = XCFileUtil.getExternalCacheDir(context) + File.separator + FOLDER_NAME;
        }
        return XCFileUtil.getFileList(folderPath);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/20
     * @description 删除log
     */
    public synchronized static boolean deleteLog(File file) {
        return XCFileUtil.deleteFile(file);
    }

    /**
     * @param compareDay 多少天前
     * @author ZhangXuanChen
     * @date 2020/2/20
     * @description 删除log
     */
    public synchronized static boolean deleteLog(Context context, int compareDay) {
        return deleteLog(context, null, compareDay);
    }

    /**
     * @param compareDay 多少天前
     * @author ZhangXuanChen
     * @date 2020/2/20
     * @description 删除log
     */
    public synchronized static boolean deleteLog(String folderPath, int compareDay) {
        return deleteLog(null, folderPath, compareDay);
    }

    /**
     * @param compareDay 多少天前
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/20
     * @description 删除log
     */
    private synchronized static boolean deleteLog(Context context, String folderPath, int compareDay) {
        boolean isSucceed = false;
        List<File> fileList = getFileList(context, folderPath);
        if (fileList != null && !fileList.isEmpty()) {
            SimpleDateFormat simpleDateFormat = XCTimeUtil.getSimpleDateFormat(XCTimeUtil.FORMAT_DATE);
            for (int i = fileList.size() - 1; i >= 0; i--) {
                File file = fileList.get(i);
                if (file != null) {
                    int tempDay = XCTimeUtil.compareDay(file.getName().replaceAll(".txt", ""), XCTimeUtil.getCurrentTime(simpleDateFormat), simpleDateFormat);
                    if (tempDay > compareDay) {
                        isSucceed = XCFileUtil.deleteFile(file.getAbsolutePath());
                    }
                }
            }
        }
        return isSucceed;
    }
}
