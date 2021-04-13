package com.xc.framework.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Date：2021/4/13
 * Author：ZhangXuanChen
 * Description：数据库监听
 */
public interface OnDBListener {
    /**
     * Author：ZhangXuanChen
     * Time：2021/4/13 15:25
     * Description：创建
     */
    void onCreate(SQLiteDatabase db);

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/13 15:25
     * Description：更新
     */
    void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion);
}
