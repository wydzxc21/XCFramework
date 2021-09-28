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
    private final String TAG = "DBHelper";
    public static DBHelper mDBHelper;
    // 数据库名称
    private static String DB_NAME = "xcFramework.db";
    // 数据库版本号
    private static int DB_VERSION = 1;
    private OnDBListener onDBListener;

    /**
     * 单例模式
     *
     * @param context
     * @return
     */
    public static DBHelper getInstance(Context context) {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(context, DB_NAME, DB_VERSION);
            mDBHelper.getReadableDatabase();//触发创建数据库
        }
        return mDBHelper;
    }

    public DBHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (onDBListener != null) {
            onDBListener.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (onDBListener != null) {
            onDBListener.onUpdate(db, oldVersion, newVersion);
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/13 15:26
     * Description：设置数据库监听
     */
    public void setOnDBListener(OnDBListener onDBListener) {
        this.onDBListener = onDBListener;
    }

    /**
     * 初始化数据库
     *
     * @param context
     * @param dbVersion 数据库版本，1
     */
    public static void initDB(Context context, int dbVersion) {
        initDB(context, DBHelper.DB_NAME, dbVersion);
    }

    /**
     * 初始化数据库
     *
     * @param context
     * @param dbName    数据库名，test.db
     * @param dbVersion 数据库版本，1
     */
    public static void initDB(Context context, String dbName, int dbVersion) {
        DBHelper.DB_NAME = dbName;
        DBHelper.DB_VERSION = dbVersion;
        getInstance(context);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/13 14:06
     * Description：删除数据库
     */
    public boolean deleteDB(Context context) {
        return deleteDB(context, DB_NAME);
    }

    /**
     * 删除数据库
     *
     * @param context
     * @param dbName  数据库名，test.db
     * @return
     */
    public boolean deleteDB(Context context, String dbName) {
        return context.deleteDatabase(dbName);
    }

}
