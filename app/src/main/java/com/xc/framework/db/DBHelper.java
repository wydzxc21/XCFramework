package com.xc.framework.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author ZhangXuanChen
 * @date 2015-9-17
 * @package com.xc.framework.db
 * @description 数据库助手类-创建,删除
 */
public class DBHelper extends SQLiteOpenHelper {
    public static DBHelper mDBHelper;
    // 数据库名称
    private static String DB_NAME = "xcFramework.db";
    // 数据库版本号
    private static int DB_VERSION = 1;

    /**
     * 单例模式
     *
     * @param context
     * @return
     * @deprecated 根据bean成员变量创建数据库表字段(类型只支持String), 表名为bean名
     */
    public static DBHelper getInstance(Context context) {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(context, DB_NAME);
        }
        return mDBHelper;
    }

    /**
     * 初始化数据库
     *
     * @param context
     * @param dbName  数据库名，text.db
     * @return
     * @deprecated 根据bean成员变量创建数据库表字段(类型只支持String), 表名为bean名
     */
    public static void init(Context context, String dbName) {
        DBHelper.DB_NAME = dbName;
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(context, dbName);
        }
    }

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 删除数据库
     *
     * @param context
     * @param dbName  数据库名，text.db
     * @return
     */
    public boolean deleteDB(Context context, String dbName) {
        return context.deleteDatabase(dbName);
    }

    public boolean deleteDB(Context context) {
        return deleteDB(context, DB_NAME);
    }
}
