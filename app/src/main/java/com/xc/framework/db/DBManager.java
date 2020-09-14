package com.xc.framework.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    public static final String KEY_ID = "_id";

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
     * 创建数据库表
     *
     * @param tableClass 以实体类名创建表名,成员变量创建字段(只支持String类型变量,相同类名不会重复创建表)
     * @return
     */
    public synchronized boolean createTable(Class<?> tableClass) {
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        if (db != null && tableClass != null) {
            try {
                db.execSQL(getCreateTableSql(tableClass));
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
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 创建数据库（非必需调用）
     */
    public synchronized void createDB(String dbName) {
        if (!XCStringUtil.isEmpty(dbName)) {
            DBHelper.init(context, dbName);
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
        if (dbHelper != null) {
            try {
                if (!XCStringUtil.isEmpty(dbName)) {
                    return dbHelper.deleteDB(context, dbName);
                } else {
                    return dbHelper.deleteDB(context);
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
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
        String insertSql = getInsertSql(classObjectList, conditionObject);
        if (db == null || XCStringUtil.isEmpty(insertSql)) {
            return false;
        }
        try {
            db.execSQL(insertSql);
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
        String condition = getKeyEqualValueSql(classObject, "and");
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
        String deleteSql = getDeleteSql(field, classObjectList);
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
        String updateSql = getUpdateSql(updateObject, conditionObject);
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
        String querySql = getQuerySql(classObject, -1, -1, null, null);
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
                return cursor.getString(cursor.getColumnIndex(KEY_ID));
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
        return query(classObject, -1, -1, null, null, null);
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
    public synchronized <T> List<T> query(T classObject, String field, String like) {
        return query(classObject, -1, -1, field, like, null);
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
    public synchronized <T> List<T> query(T classObject, int limit, int offset) {
        return query(classObject, limit, offset, null, null, null);
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
            return query(classObject.newInstance(), -1, -1, null, null, sqlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询
     *
     * @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,
     *                    new空对象查询该表所有数据 )
     * @param limit       分页查询-获取数量
     * @param offset      分页查询-其实索引(从0开始)
     * @param field       模糊查询-字段名
     * @param like        模糊查询-包含字符串
     * @param sqlStr      自定义sql语句
     */
    private synchronized <T> List<T> query(T classObject, int limit, int offset, String field, String like, String sqlStr) {
        if (classObject == null) {
            return null;
        }
        if (!isTableExist(classObject.getClass())) {
            return null;
        }
        if (XCStringUtil.isEmpty(sqlStr)) {
            sqlStr = getQuerySql(classObject, limit, offset, field, like);
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
                    T newClassObject = parseClassObject(cursor, classObject, false);
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
        String queryJoinSql = getQueryJoinSql(joinType, masterClass, masterField, slaveMap);
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
                    String masterId = cursor.getString(cursor.getColumnIndex(masterClass.getSimpleName() + KEY_ID));
                    T masterObject = parseClassObject(cursor, masterClass.newInstance(), true);
                    masterTempMap.put(masterId, masterObject);
                    //
                    for (Class<?> key : slaveMap.keySet()) {
                        String salveId = cursor.getString(cursor.getColumnIndex(key.getSimpleName() + KEY_ID));
                        if (!XCStringUtil.isEmpty(salveId)) {
                            Object slaveObject = parseClassObject(cursor, key.newInstance(), true);
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

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/18 16:24
     * Description：parseClassObject
     */
    private <T> T parseClassObject(Cursor cursor, T classObject, boolean isAlias) {
        try {
            Map<String, String> fieldNameMap = XCBeanUtil.getFieldNameMap(classObject.getClass());
            T newClassObject = (T) classObject.getClass().newInstance();
            if (fieldNameMap != null && !fieldNameMap.isEmpty()) {
                for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
                    String primitiveName = !XCStringUtil.isEmpty(entry.getKey()) ? entry.getKey() : "";
                    String aliasName = !XCStringUtil.isEmpty(entry.getValue()) ? entry.getValue() : "";
                    //优先采用别名，无别名再采用原名
                    String name = !XCStringUtil.isEmpty(aliasName) ? aliasName : primitiveName;
                    // key
                    String key;
                    if (isAlias) {
                        key = classObject.getClass().getSimpleName() + name;
                    } else {
                        key = name;
                    }
                    // value
                    String value = cursor.getString(cursor.getColumnIndex(key));
                    XCBeanUtil.invokeSetMethod(newClassObject, primitiveName, value);
                }
            }
            return newClassObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ------------------------------------------------生成sql语句方法--------------------------------------------------

    /**
     * 根据实体类生成创建数据库表sql语句
     *
     * @param tableClass
     * @return
     */
    private String getCreateTableSql(Class<?> tableClass) {
        String sql = "";
        Map<String, String> fieldNameMap = XCBeanUtil.getFieldNameMap(tableClass);
        if (fieldNameMap != null && !fieldNameMap.isEmpty()) {
            for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
                String primitiveName = !XCStringUtil.isEmpty(entry.getKey()) ? entry.getKey() : "";
                String aliasName = !XCStringUtil.isEmpty(entry.getValue()) ? entry.getValue() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(aliasName) ? aliasName : primitiveName;
                if (!name.equals(KEY_ID)) {
                    sql += "," + name + " text";
                }
            }
        }
        return "create table if not exists " + tableClass.getSimpleName() + "(" + KEY_ID + " integer not null primary key autoincrement" + sql + ")";
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/5/22 15:28
     * Description：获取插入sql语句
     */
    private <T> String getInsertSql(List<T> classObjectList, T conditionObject) {
        if (classObjectList != null && !classObjectList.isEmpty()) {
            String key = "";
            String value = "";
            for (int i = 0; i < classObjectList.size(); i++) {
                String[] keyAndValueSql = getKeyAndValueSql(classObjectList.get(i));
                if (i == 0) {
                    key = "(" + keyAndValueSql[0] + ")";
                }
                if (conditionObject != null) {
                    value += "" + keyAndValueSql[1] + ",";
                } else {
                    value += "(" + keyAndValueSql[1] + "),";
                }
            }
            value = value.substring(0, value.length() - 1);
            String insertSql;
            String querySql = "";
            if (conditionObject != null) {
                insertSql = "insert into " + classObjectList.get(0).getClass().getSimpleName() + " " + key + " select " + value;
                querySql = " where not exists ( " + getQuerySql(conditionObject, -1, -1, null, null) + " )";
            } else {
                insertSql = "insert into " + classObjectList.get(0).getClass().getSimpleName() + " " + key + " values " + value;
            }
            return insertSql + querySql;
        }
        return "";
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/5/23 9:15
     * Description：获取删除sql语句
     */
    public <T> String getDeleteSql(String field, List<T> classObjectList) {
        if (!XCStringUtil.isEmpty(field) && classObjectList != null && !classObjectList.isEmpty()) {
            String key = "";
            String value = "";
            for (int i = 0; i < classObjectList.size(); i++) {
                Map<String, String> fieldNameMap = XCBeanUtil.getFieldNameMap(classObjectList.get(i).getClass());
                if (fieldNameMap != null && !fieldNameMap.isEmpty()) {
                    for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
                        String primitiveName = !XCStringUtil.isEmpty(entry.getKey()) ? entry.getKey() : "";
                        String aliasName = !XCStringUtil.isEmpty(entry.getValue()) ? entry.getValue() : "";
                        //优先采用别名，无别名再采用原名
                        String name = !XCStringUtil.isEmpty(aliasName) ? aliasName : primitiveName;
                        if (field.equals(primitiveName) || field.equals(aliasName)) {
                            // key
                            key = name;
                            String tempVal = "" + XCBeanUtil.invokeGetMethod(classObjectList.get(i), primitiveName);
                            if (!XCStringUtil.isEmpty(tempVal)) {
                                value += "'" + tempVal + "',";
                            }
                        }
                    }
                }
            }
            value = value.substring(0, value.length() - 1);
            return "delete from " + classObjectList.get(0).getClass().getSimpleName() + " where " + key + " in (" + value + ")";
        }
        return "";
    }

    /**
     * 根据类对象生成-更新sql语句
     *
     * @param updateObject
     * @param conditionObject
     * @return
     */
    private String getUpdateSql(Object updateObject, Object conditionObject) {
        String updateSql = getKeyEqualValueSql(updateObject, ",");
        String conditionSql = getKeyEqualValueSql(conditionObject, "and");
        if (XCStringUtil.isEmpty(conditionSql)) {
            return "update " + conditionObject.getClass().getSimpleName() + " set " + updateSql;
        } else {
            return "update " + conditionObject.getClass().getSimpleName() + " set " + updateSql + " where " + conditionSql;
        }
    }

    /**
     * 根据类对象生成-查询sql语句
     *
     * @param classObject 类对象
     * @param limit       分页查询-获取数量
     * @param offset      分页查询-其实索引(从0开始)
     * @param field       模糊查询-字段名
     * @param like        模糊查询-包含字符串
     * @return
     */
    private String getQuerySql(Object classObject, int limit, int offset, String field, String like) {
        if (limit >= 0 && offset >= 0) {//分页查询
            return "select * from " + classObject.getClass().getSimpleName() + " limit " + limit + " offset " + offset;
        } else if (!XCStringUtil.isEmpty(field) && !XCStringUtil.isEmpty(like)) {//模糊查询
            return "select * from " + classObject.getClass().getSimpleName() + " where " + field + " like '%" + like + "%'";
        } else {//条件查询
            String condition = getKeyEqualValueSql(classObject, "and");
            if (XCStringUtil.isEmpty(condition)) {
                return "select * from " + classObject.getClass().getSimpleName();
            } else {
                return "select * from " + classObject.getClass().getSimpleName() + " where " + condition;
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/18 15:17
     * Description：连表查sql语句
     * Param：masterClass 主类
     * Param：slaveObjectMap 副类集合
     * Return：java.lang.String
     */
    private String getQueryJoinSql(String joinType, Class<?> masterClass, String masterField, Map<Class<?>, String> slaveMap) {
        String joinCondition = "";
        String joinField = getJoinFieldAliasSql(masterClass);
        int i = 0;
        for (Class<?> key : slaveMap.keySet()) {
            joinField += "," + getJoinFieldAliasSql(key);
            if (i == 0) {
                joinCondition = masterClass.getSimpleName() + " " + joinType + " join " + key.getSimpleName() + " on " + masterClass.getSimpleName() + "." + masterField + " = " + key.getSimpleName() + "." + slaveMap.get(key);
            } else {
                joinCondition = "(" + joinCondition + ")" + " " + joinType + " join " + key.getSimpleName() + " on " + masterClass.getSimpleName() + "." + masterField + " = " + key.getSimpleName() + "." + slaveMap.get(key);
            }
            i++;
        }
        return "select " + joinField + " from " + joinCondition;
    }

    /**
     * 获取连表字段别名sql语句
     * <p>
     * Param：masterClass 主类
     * Param：slaveObjectMap 副类集合
     */
    private String getJoinFieldAliasSql(Class<?> joinClass) {
        String condition = joinClass.getSimpleName() + "." + KEY_ID + " as " + joinClass.getSimpleName() + KEY_ID + ",";
        Map<String, String> fieldNameMap = XCBeanUtil.getFieldNameMap(joinClass);
        if (fieldNameMap != null && !fieldNameMap.isEmpty()) {
            for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
                String primitiveName = !XCStringUtil.isEmpty(entry.getKey()) ? entry.getKey() : "";
                String aliasName = !XCStringUtil.isEmpty(entry.getValue()) ? entry.getValue() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(aliasName) ? aliasName : primitiveName;
                // key
                String key = name;
                //
                condition += joinClass.getSimpleName() + "." + key + " as " + joinClass.getSimpleName() + key + ",";
            }
            if (!XCStringUtil.isEmpty(condition)) {
                condition = condition.substring(0, condition.length() - 1);
            }
        }
        return condition;
    }

    /**
     * 获取key等于value的sql语句
     *
     * @param classObject 类对象
     * @param connectFlag ","或"and"
     * @return key = 'value' connectFlag key = 'value'
     */
    private static String getKeyEqualValueSql(Object classObject, String connectFlag) {
        String condition = "";
        Map<String, String> fieldNameMap = XCBeanUtil.getFieldNameMap(classObject.getClass());
        if (fieldNameMap != null && !fieldNameMap.isEmpty()) {
            for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
                String primitiveName = !XCStringUtil.isEmpty(entry.getKey()) ? entry.getKey() : "";
                String aliasName = !XCStringUtil.isEmpty(entry.getValue()) ? entry.getValue() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(aliasName) ? aliasName : primitiveName;
                // key
                String key = name;
                // value
                String value = "" + XCBeanUtil.invokeGetMethod(classObject, primitiveName);
                if (!XCStringUtil.isEmpty(value)) {
                    condition += key + " = '" + value + "' " + connectFlag + " ";
                }
            }
            if (!XCStringUtil.isEmpty(condition)) {
                if (",".equals(connectFlag)) {
                    condition = condition.substring(0, condition.length() - 3);
                } else if ("and".equals(connectFlag)) {
                    condition = condition.substring(0, condition.length() - 5);
                }
            }
        }
        return condition;
    }

    /**
     * 获取key与value的sql语句
     *
     * @param classObject 类对象
     * @return key, key与value, value
     */
    private String[] getKeyAndValueSql(Object classObject) {
        String key = "";
        String value = "";
        Map<String, String> fieldNameMap = XCBeanUtil.getFieldNameMap(classObject.getClass());
        if (fieldNameMap != null && !fieldNameMap.isEmpty()) {
            for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
                String primitiveName = !XCStringUtil.isEmpty(entry.getKey()) ? entry.getKey() : "";
                String aliasName = !XCStringUtil.isEmpty(entry.getValue()) ? entry.getValue() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(aliasName) ? aliasName : primitiveName;
                if (!name.equals(KEY_ID)) {
                    // key
                    String tempValue = "" + XCBeanUtil.invokeGetMethod(classObject, primitiveName);
                    String getValue = !XCStringUtil.isEmpty(tempValue) ? tempValue : "";
                    key += name + ",";
                    // value
                    value += "'" + getValue + "',";
                }
            }
            key = key.substring(0, key.length() - 1);
            value = value.substring(0, value.length() - 1);
        }
        return new String[]{key, value};
    }

}
