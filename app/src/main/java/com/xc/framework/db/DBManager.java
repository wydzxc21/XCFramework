package com.xc.framework.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xc.framework.bean.FieldBean;
import com.xc.framework.util.XCArrayUtil;
import com.xc.framework.util.XCBeanUtil;
import com.xc.framework.util.XCStringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangXuanChen
 * @date 2015-9-22
 * @package com.xc.framework.utils
 * @description 数据库管理类
 */
public class DBManager {
    public static final String TAG = "DBManager";
    private Context context;
    public static DBManager mDBManager;


    public DBManager(Context context) {
        this.context = context;
    }

    public static synchronized DBManager getInstance(Context context) {
        if (mDBManager == null) {
            mDBManager = new DBManager(context);
        }
        return mDBManager;
    }

    /**
     * @param sql sql语句
     * @author ZhangXuanChen
     * @date 2022/3/11 09:03
     * @description 执行sql语句
     */
    public synchronized boolean execSQL(String sql) {
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        if (db != null && !XCStringUtil.isEmpty(sql)) {
            try {
                db.execSQL(sql);
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
        return false;
    }

    /**
     * 创建数据库表
     *
     * @param tableClass 以实体类名创建表名,成员变量创建字段(只支持String类型变量,相同类名不会重复创建表)
     * @return
     */
    public synchronized boolean createTable(Class<?> tableClass) {
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        if (db != null && tableClass != null) {
            try {
                db.execSQL(DBUtil.getCreateTableSql(tableClass));
            } catch (Exception e) {
                return false;
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 数据库表是否存在
     *
     * @param tableClass 以实体类名创建的表
     * @return
     */
    public synchronized boolean isTableExist(Class<?> tableClass) {
        if (tableClass == null) {
            return false;
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String sql = "select count(*) as c from sqlite_master where type = 'table' and name = '" + tableClass.getSimpleName() + "'";
        if (db == null || XCStringUtil.isEmpty(sql)) {
            return false;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor == null) {
                return false;
            }
            if (cursor.moveToNext()) {
                if (cursor.getInt(0) > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2022/3/11 9:20
     * Description：重命名表名
     */
    public synchronized boolean renameTable(String oldTableName, String newTableName) {
        if (XCStringUtil.isEmpty(oldTableName) || XCStringUtil.isEmpty(oldTableName)) {
            return false;
        }
        SQLiteDatabase db = null;
        try {
            db = DBHelper.getInstance(context).getReadableDatabase();
            String renameSql = "alter table " + oldTableName + " rename to " + newTableName;
            db.execSQL(renameSql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/9 15:33
     * Description：变更表结果-并复制表数据
     */
    public synchronized boolean alterTable(Class<?> tableClass) {
        if (tableClass == null) {
            return false;
        }
        if (!isTableExist(tableClass)) {
            return false;
        }
        SQLiteDatabase db = null;
        try {
            db = DBHelper.getInstance(context).getReadableDatabase();
            List<String> oldList = DBUtil.getAlterTableField(db, tableClass);
            List<FieldBean> newList = XCBeanUtil.getFieldList(tableClass);
            if (oldList == null || oldList.isEmpty() || newList == null || newList.isEmpty()) {
                return false;
            }
            List<String> equalList = DBUtil.getAlterEqualField(oldList, newList);
            if (equalList.size() == oldList.size() && equalList.size() == newList.size()) {//未变更表结构
                return true;
            }
            //将表改为临时表
            String oldTable = tableClass.getSimpleName() + "_" + System.currentTimeMillis();
            String renameSql = "alter table " + tableClass.getSimpleName() + " rename to " + oldTable;
            db.execSQL(renameSql);
            //创建新表
            String createSql = DBUtil.getCreateTableSql(tableClass);
            db.execSQL(createSql);
            //导入数据
            String importSql = DBUtil.getImportSql(oldTable, tableClass, equalList);
            if (!XCStringUtil.isEmpty(importSql)) {
                db.execSQL(importSql);
            }
            //删除临时表
            String deleteSql = "drop table " + oldTable;
            db.execSQL(deleteSql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 删除数据库表
     *
     * @param tableClass 以实体类名创建的表
     * @return
     */
    public synchronized boolean deleteTable(Class<?> tableClass) {
        if (tableClass == null) {
            return false;
        }
        if (!isTableExist(tableClass)) {
            return false;
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String sql = "drop table " + tableClass.getSimpleName();
        if (db == null || XCStringUtil.isEmpty(sql)) {
            return false;
        }
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/19 9:39
     * Description：清空数据库表
     *
     * @param tableClass 以实体类名创建的表
     */
    public synchronized boolean clearTable(Class<?> tableClass) {
        if (tableClass == null) {
            return false;
        }
        if (!isTableExist(tableClass)) {
            return false;
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String sql = "delete from " + tableClass.getSimpleName();
        if (db == null || XCStringUtil.isEmpty(sql)) {
            return false;
        }
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * @param dbVersion 数据库版本，1
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 初始化数据库（非必需调用）
     */
    public synchronized void initDB(int dbVersion) {
        if (dbVersion > 0) {
            DBHelper.initDB(context, dbVersion);
        }
    }

    /**
     * @param dbName    数据库名，test.db
     * @param dbVersion 数据库版本，1
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 初始化数据库（非必需调用）
     */
    public synchronized void initDB(String dbName, int dbVersion) {
        if (!XCStringUtil.isEmpty(dbName) && dbVersion > 0) {
            DBHelper.initDB(context, dbName, dbVersion);
        }
    }

    /**
     * 删除数据库
     *
     * @return
     */
    public synchronized boolean deleteDB() {
        return deleteDB(null);
    }

    /**
     * 删除数据库
     *
     * @return
     */
    public synchronized boolean deleteDB(String dbName) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            if (!XCStringUtil.isEmpty(dbName)) {
                return dbHelper.deleteDB(context, dbName);
            } else {
                return dbHelper.deleteDB(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 插入
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取插入数据,只支持String变量(完全相同的数据不会重复插入)
     * @return
     */
    public synchronized <T> boolean insert(T classObject) {
        List<T> classObjectList = new ArrayList<T>();
        classObjectList.add(classObject);
        return insert(classObjectList, null);
    }

    /**
     * 插入
     *
     * @param classObject     类对象,操作以该对象类名创建的表,反射get方法获取插入数据,只支持String变量(完全相同的数据不会重复插入)
     * @param conditionObject 条件对象，存在不插入
     * @return
     */
    public synchronized <T> boolean insert(T classObject, T conditionObject) {
        List<T> classObjectList = new ArrayList<T>();
        classObjectList.add(classObject);
        return insert(classObjectList, conditionObject);
    }

    /**
     * 插入
     *
     * @param classObjectList 类对象集合,操作以该对象类名创建的表,反射get方法获取插入数据,只支持String变量(完全相同的数据不会重复插入)
     * @return
     */
    public synchronized <T> boolean insert(List<T> classObjectList) {
        return insert(classObjectList, null);
    }

    /**
     * 插入
     *
     * @param classObjectList 类对象集合,操作以该对象类名创建的表,反射get方法获取插入数据,只支持String变量(完全相同的数据不会重复插入)
     * @param conditionObject 条件对象，存在不插入
     * @return
     */
    private synchronized <T> boolean insert(List<T> classObjectList, T conditionObject) {
        if (classObjectList == null || classObjectList.isEmpty()) {
            return false;
        }
        if (!isTableExist(classObjectList.get(0).getClass())) {
            createTable(classObjectList.get(0).getClass());
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        List<List<T>> splitObjectList = new ArrayList<List<T>>();
        if (classObjectList.size() > 500) {//sqlite每次插入上限500
            splitObjectList.addAll(XCArrayUtil.split(classObjectList, 500));
        } else {
            splitObjectList.add(classObjectList);
        }
        try {
            for (int i = 0; i < splitObjectList.size(); i++) {
                String insertSql = DBUtil.getInsertSql(splitObjectList.get(i), conditionObject);
                if (db != null && !XCStringUtil.isEmpty(insertSql)) {
                    db.execSQL(insertSql);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 删除
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取删除条件(条件唯一删除唯一一条数据,条件不唯一删除符合条件的所有数据,
     *                    new空对象删除该表所有数据 )
     * @return
     */
    public synchronized <T> boolean delete(T classObject) {
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String condition = DBUtil.getKeyEqualValueSql(classObject, "and", false);
        String deleteSql = "delete from " + classObject.getClass().getSimpleName();
        if (!XCStringUtil.isEmpty(condition)) {
            deleteSql += " where " + condition;
        }
        if (db != null && !XCStringUtil.isEmpty(deleteSql)) {
            try {
                db.execSQL(deleteSql);
            } catch (Exception e) {
                return false;
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 删除
     *
     * @param classObjectList 类对象集合,操作以该对象类名创建的表,反射get方法获取删除条件(条件唯一删除唯一一条数据,条件不唯一删除符合条件的所有数据,
     *                        new空对象删除该表所有数据 )
     * @return
     */
    public synchronized <T> boolean delete(String field, List<T> classObjectList) {
        if (XCStringUtil.isEmpty(field) || classObjectList == null || classObjectList.isEmpty()) {
            return false;
        }
        if (!isTableExist(classObjectList.get(0).getClass())) {
            return false;
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String deleteSql = DBUtil.getDeleteSql(field, classObjectList);
        if (db == null || XCStringUtil.isEmpty(deleteSql)) {
            return false;
        }
        try {
            db.execSQL(deleteSql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 更新
     *
     * @param updateObject    更新数据类对象,反射get方法获取更新数据(要与查询条件类对象为相同类的对象)
     * @param conditionObject 查询条件类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一更新唯一一条数据,
     *                        条件不唯一更新符合条件的所有数据, new空对象更新该表所有数据)
     * @return
     */
    public synchronized <T> boolean update(T updateObject, T conditionObject) {
        if (updateObject == null || conditionObject == null) {
            return false;
        }
        if (!isTableExist(updateObject.getClass())) {
            return false;
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String updateSql = DBUtil.getUpdateSql(updateObject, conditionObject);
        if (db == null || XCStringUtil.isEmpty(updateSql)) {
            return false;
        }
        try {
            db.execSQL(updateSql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/5/22 16:28
     * Description：查询总条数
     */
    public synchronized <T> int queryTotalCount(T classObject) {
        if (classObject == null) {
            return -1;
        }
        if (!isTableExist(classObject.getClass())) {
            return -1;
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String sql = "select count(*) from " + classObject.getClass().getSimpleName();
        if (db == null || XCStringUtil.isEmpty(sql)) {
            return -1;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor == null) {
                return -1;
            }
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/25
     * @description 查询主键id
     */
    public synchronized <T> String queryKeyId(T classObject) {
        if (classObject == null) {
            return null;
        }
        if (!isTableExist(classObject.getClass())) {
            return null;
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String querySql = DBUtil.getQuerySql(classObject, -1, -1, null, null, null, null);
        if (db == null || XCStringUtil.isEmpty(querySql)) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(querySql, null);
            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(DBUtil.KEY_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 条件查询
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     * @return 结果集                 new空对象查询该表所有数据 )
     */
    public synchronized <T> List<T> query(T classObject) {
        return query(classObject, -1, -1, null, null, null, null, null);
    }

    /**
     * 语句查询
     *
     * @param classObject 类,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @param sqlStr      自定义sql语句
     * @return 结果集
     */
    public synchronized <T> List<T> query(Class<T> classObject, String sqlStr) {
        try {
            return query(classObject.newInstance(), -1, -1, null, null, null, null, sqlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 分页+条件查询
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @param limit       分页查询-获取数量
     * @param offset      分页查询-起始索引(从0开始)
     * @return 结果集
     */
    public synchronized <T> List<T> query(T classObject, int limit, int offset) {
        return query(classObject, limit, offset, null, null, null, null, null);
    }

    /**
     * 分页+模糊查询
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @param limit       分页查询-获取数量
     * @param offset      分页查询-起始索引(从0开始)
     * @param field       模糊查询-字段名
     * @param like        模糊查询-包含字符串
     * @return 结果集
     */
    public synchronized <T> List<T> query(T classObject, int limit, int offset, String field, String like) {
        return query(classObject, limit, offset, field, like, null, null, null);
    }

    /**
     * 分页+日期查询
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @param limit       分页查询-获取数量
     * @param offset      分页查询-起始索引(从0开始)
     * @param field       日期查询-字段名
     * @param startDate   日期查询-起始日期(日期格式)
     * @param endDate     日期查询-结束日期(日期格式)
     * @return 结果集
     */
    public synchronized <T> List<T> query(T classObject, int limit, int offset, String field, String startDate, String endDate) {
        return query(classObject, limit, offset, field, null, startDate, endDate, null);
    }

    /**
     * 查询
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @param limit       分页查询-获取数量
     * @param offset      分页查询-起始索引(从0开始)
     * @param field       模糊/日期查询-字段名
     * @param like        模糊查询-包含字符串
     * @param startDate   日期查询-起始日期(日期格式)
     * @param endDate     日期查询-结束日期(日期格式)
     * @param sqlStr      自定义sql语句
     */
    private synchronized <T> List<T> query(T classObject, int limit, int offset, String field, String like, String startDate, String endDate, String sqlStr) {
        if (classObject == null) {
            return null;
        }
        if (!isTableExist(classObject.getClass())) {
            return null;
        }
        if (XCStringUtil.isEmpty(sqlStr)) {
            sqlStr = DBUtil.getQuerySql(classObject, limit, offset, field, like, startDate, endDate);
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        if (db == null || XCStringUtil.isEmpty(sqlStr)) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sqlStr, null);
            if (cursor == null) {
                return null;
            }
            List<T> mList = new ArrayList<T>();
            if (cursor.moveToFirst()) {
                do {
                    T newClassObject = DBUtil.parseClassObject(cursor, classObject, false);
                    if (newClassObject != null) {
                        mList.add(newClassObject);
                    }
                } while (cursor.moveToNext());
            }
            return mList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/20 9:48
     * Description：左联查询
     * Param：masterClass 主表类
     * Param：masterField 主表条件字段
     * Param：slaveMap 从表类与从表条件字段
     * Return：java.lang.Object 第一条为主表类数据，之后为从表类数据
     */
    public synchronized <T> Map<T, List<Object>> queryLeftJoin(Class<T> masterClass, String masterField, Map<Class<?>, String> slaveMap) {
        return queryJoin("left", masterClass, masterField, slaveMap);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/20 9:48
     * Description：内联查询
     * Param：masterClass 主表类
     * Param：masterField 主表条件字段
     * Param：slaveMap 从表类与从表条件字段
     * Return：java.lang.Object 第一条为主表类数据，之后为从表类数据
     */
    public synchronized <T> Map<T, List<Object>> queryInnerJoin(Class<T> masterClass, String masterField, Map<Class<?>, String> slaveMap) {
        return queryJoin("inner", masterClass, masterField, slaveMap);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/20 9:48
     * Description：连表查询
     * Param：joinType 联接类型
     * Param：masterClass 主表类
     * Param：masterField 主表条件字段
     * Param：slaveMap 从表类与从表条件字段
     * Return：java.lang.Object 第一条为主表类数据，之后为从表类数据
     */
    private synchronized <T> Map<T, List<Object>> queryJoin(String joinType, Class<T> masterClass, String masterField, Map<Class<?>, String> slaveMap) {
        if (masterClass == null || XCStringUtil.isEmpty(masterField) || slaveMap == null || slaveMap.isEmpty()) {
            return null;
        }
        if (!isTableExist(masterClass)) {
            return null;
        }
        for (Class<?> key : slaveMap.keySet()) {
            if (!isTableExist(key)) {
                return null;
            }
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String queryJoinSql = DBUtil.getQueryJoinSql(joinType, masterClass, masterField, slaveMap);
        if (db == null || XCStringUtil.isEmpty(queryJoinSql)) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(queryJoinSql, null);
            if (cursor == null) {
                return null;
            }
            Map<T, List<Object>> mAllMap = new HashMap<T, List<Object>>();
            Map<String, T> masterTempMap = new HashMap<String, T>();
            Map<String, Object> slaveTempMap = new HashMap<String, Object>();
            if (cursor.moveToFirst()) {
                do {
                    String masterId = cursor.getString(cursor.getColumnIndex(masterClass.getSimpleName() + DBUtil.KEY_ID));
                    T masterObject = DBUtil.parseClassObject(cursor, masterClass.newInstance(), true);
                    masterTempMap.put(masterId, masterObject);
                    //
                    for (Class<?> key : slaveMap.keySet()) {
                        String salveId = cursor.getString(cursor.getColumnIndex(key.getSimpleName() + DBUtil.KEY_ID));
                        if (!XCStringUtil.isEmpty(salveId)) {
                            Object slaveObject = DBUtil.parseClassObject(cursor, key.newInstance(), true);
                            slaveTempMap.put(masterId + "/" + slaveObject.getClass().getSimpleName() + salveId, slaveObject);
                        }
                    }
                } while (cursor.moveToNext());
            }
            //
            for (Map.Entry<String, T> master : masterTempMap.entrySet()) {
                List<Object> mList = new ArrayList<Object>();
                for (Map.Entry<String, Object> slave : slaveTempMap.entrySet()) {
                    String masterId = XCStringUtil.split(slave.getKey(), "/")[0];
                    if (master.getKey().equals(masterId)) {
                        mList.add(slave.getValue());
                    }
                }
                mAllMap.put(master.getValue(), mList);
            }
            return mAllMap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 是否存在
     *
     * @param conditionObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(有符合条件的就返回true)
     * @return
     */
    public synchronized <T> boolean isExist(T conditionObject) {
        List<T> queryList = query(conditionObject);
        if (queryList != null && !queryList.isEmpty()) {
            if (queryList.size() > 0) {
                return true;
            }
        }
        return false;
    }

}
