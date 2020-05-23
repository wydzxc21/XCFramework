package com.xc.framework.util;

import android.content.Context;

import com.xc.framework.db.DBManager;

import java.util.List;

/**
 * @author ZhangXuanChen
 * @date 2016-11-13
 * @package com.xc.framework.utils
 * @description 数据库操作工具类
 */
public class XCDBUtil {
    /**
     * 创建数据库表
     *
     * @param context    上下文
     * @param tableClass 以实体类名创建表名,成员变量创建字段(只支持String类型变量,相同类名不会重复创建表)
     * @return 是否成功
     */
    public static boolean createTable(Context context, Class<?> tableClass) {
        return DBManager.getInstance(context).createTable(tableClass);
    }

    /**
     * 数据库表是否存在
     *
     * @param context    上下文
     * @param tableClass 以实体类名创建的表
     * @return 是否成功
     */
    public static boolean isTableExist(Context context, Class<?> tableClass) {
        return DBManager.getInstance(context).isTableExist(tableClass);
    }

    /**
     * 清空数据库表
     *
     * @param context    上下文
     * @param tableClass 以实体类名创建的表
     * @return 是否成功
     */
    public static boolean clearTable(Context context, Class<?> tableClass) {
        return DBManager.getInstance(context).clearTable(tableClass);
    }

    /**
     * 删除数据库表
     *
     * @param context    上下文
     * @param tableClass 以实体类名创建的表
     * @return 是否成功
     */
    public static boolean deleteTable(Context context, Class<?> tableClass) {
        return DBManager.getInstance(context).deleteTable(tableClass);
    }

    /**
     * 创建数据库（非必调）
     *
     * @param context 上下文
     * @return 是否成功
     */
    public static void createDB(Context context, String dbName) {
        DBManager.getInstance(context).createDB(dbName);
    }

    /**
     * 删除数据库
     *
     * @param context 上下文
     * @return 是否成功
     */
    public static boolean deleteDB(Context context) {
        return DBManager.getInstance(context).deleteDB();
    }


    /**
     * 删除数据库
     *
     * @param context 上下文
     * @param dbName  数据库名(含后缀名)
     * @return 是否成功
     */
    public static boolean deleteDB(Context context, String dbName) {
        return DBManager.getInstance(context).deleteDB(dbName);
    }

    /**
     * 插入
     *
     * @param context     上下文
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取插入数据,只支持String变量(完全相同的数据不会重复插入)
     * @return 是否成功
     */
    public static <T> boolean insert(Context context, T classObject) {
        return DBManager.getInstance(context).insert(classObject);
    }

    /**
     * 插入
     *
     * @param context         上下文
     * @param classObjectList 类对象集合,操作以该对象类名创建的表,反射get方法获取插入数据,只支持String变量(完全相同的数据不会重复插入)
     * @return 是否成功
     */
    public static <T> boolean insert(Context context, List<T> classObjectList) {
        return DBManager.getInstance(context).insert(classObjectList);
    }

    /**
     * 删除
     *
     * @param context     上下文
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取删除条件(条件唯一删除唯一一条数据,条件不唯一删除符合条件的所有数据,
     *                    new空对象删除该表所有数据 )
     * @return 是否成功
     */
    public static <T> boolean delete(Context context, T classObject) {
        return DBManager.getInstance(context).delete(classObject);
    }

    /**
     * 删除
     *
     * @param context         上下文
     * @param field           集合中以此字段名为删除条件
     * @param classObjectList 类对象集合,操作以该对象类名创建的表,反射get方法获取删除条件(条件唯一删除唯一一条数据,条件不唯一删除符合条件的所有数据,
     *                        new空对象删除该表所有数据 )
     * @return 是否成功
     */
    public static <T> boolean delete(Context context, String field, List<T> classObjectList) {
        return DBManager.getInstance(context).delete(field, classObjectList);
    }

    /**
     * 更新
     *
     * @param context         上下文
     * @param updateObject    更新数据类对象,反射get方法获取更新数据(要与查询条件类对象为相同类的对象)
     * @param conditionObject 查询条件类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一更新唯一一条数据,
     *                        条件不唯一更新符合条件的所有数据, new空对象更新该表所有数据)
     * @return 是否成功
     */
    public static <T> boolean update(Context context, T updateObject, T conditionObject) {
        return DBManager.getInstance(context).update(updateObject, conditionObject);
    }

    /**
     * 条件查询
     *
     * @param context     上下文
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @return 结果集
     */
    public static <T> List<T> query(Context context, T classObject) {
        return DBManager.getInstance(context).query(classObject);
    }

    /**
     * 模糊查询
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @param field       模糊查询-字段名
     * @param like        模糊查询-包含字符串
     * @return 结果集
     */
    public static <T> List<T> query(Context context, T classObject, String field, String like) {
        return DBManager.getInstance(context).query(classObject, field, like);
    }

    /**
     * 分页查询
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @param limit       分页查询-获取数量
     * @param offset      分页查询-其实索引(从0开始)
     * @return 结果集
     */
    public static <T> List<T> query(Context context, T classObject, int limit, int offset) {
        return DBManager.getInstance(context).query(classObject, limit, offset);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/25
     * @description 查询总条数
     */
    public static <T> int queryTotalCount(Context context, T classObject) {
        return DBManager.getInstance(context).queryTotalCount(classObject);
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/25
     * @description 查询主键id
     */
    public static <T> String queryKeyId(Context context, T classObject) {
        return DBManager.getInstance(context).queryKeyId(classObject);
    }

    /**
     * 是否存在
     *
     * @param context     上下文
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(有符合条件的就返回true)
     * @return 是否存在
     */
    public static <T> boolean isExist(Context context, T classObject) {
        return DBManager.getInstance(context).isExist(classObject);
    }
}
