package com.xc.framework.util;

import android.content.Context;

import com.xc.framework.sp.SPManager;

/**
 * @author ZhangXuanChen
 * @date 2020/2/4
 * @package com.xc.framework.util
 * @description xml操作工具
 */
public class XCSPUtil {

    /**
     * @param spName xml文件名，text
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 初始化
     */
    public static void init(Context context, String spName) {
        SPManager.init(context, spName);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 重命名
     */
    public static void rename(Context context, String oldKey, String newKey) {
        SPManager.getInstance(context).rename(oldKey, newKey);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 重命名
     */
    public static <T> void rename(Context context, String oldClassName, Class<T> objectClass) {
        SPManager.getInstance(context).rename(oldClassName, objectClass);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 保存
     */
    public static void save(Context context, String key, String value) {
        SPManager.getInstance(context).save(key, value);
    }

    /**
     * @param classObject 类对象,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 保存
     */
    public static <T> void save(Context context, T classObject) {
        SPManager.getInstance(context).save(classObject);
    }

    /**
     * @param tag         标识
     * @param classObject 类对象,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 保存
     */
    public static <T> void save(Context context, String tag, T classObject) {
        SPManager.getInstance(context).save(tag, classObject);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 获取
     */
    public static String get(Context context, String key) {
        return SPManager.getInstance(context).get(key);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 获取
     */
    public static String get(Context context, String key, String defValue) {
        return SPManager.getInstance(context).get(key, defValue);
    }

    /**
     * @param objectClass 类,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 获取
     */
    public static <T> T get(Context context, Class<T> objectClass) {
        return SPManager.getInstance(context).get(objectClass);
    }

    /**
     * @param tag         标识
     * @param objectClass 类,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 获取
     */
    public static <T> T get(Context context, String tag, Class<T> objectClass) {
        return SPManager.getInstance(context).get(tag, objectClass);
    }

    /**
     * @param objectClass 类,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description clear
     */
    public static <T> void clear(Context context, Class<T> objectClass) {
        SPManager.getInstance(context).clear(objectClass);
    }

    /**
     * @param tag         标识
     * @param objectClass 类,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description clear
     */
    public static <T> void clear(Context context, String tag, Class<T> objectClass) {
        SPManager.getInstance(context).clear(tag, objectClass);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description clearAll
     */
    public static void clearAll(Context context) {
        SPManager.getInstance(context).clearAll();
    }

    /**
     * @param objectClass 类,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description remove
     */
    public static <T> void remove(Context context, Class<T> objectClass) {
        SPManager.getInstance(context).remove(objectClass);
    }

    /**
     * @param tag         标识
     * @param objectClass 类,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description remove
     */
    public static <T> void remove(Context context, String tag, Class<T> objectClass) {
        SPManager.getInstance(context).remove(tag, objectClass);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description remove
     */
    public static void remove(Context context, String key) {
        SPManager.getInstance(context).remove(key);
    }
}
