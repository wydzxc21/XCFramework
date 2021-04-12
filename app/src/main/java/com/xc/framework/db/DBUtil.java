package com.xc.framework.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xc.framework.bean.FieldBean;
import com.xc.framework.util.XCBeanUtil;
import com.xc.framework.util.XCStringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Date：2021/4/12
 * Author：ZhangXuanChen
 * Description：数据库工具
 */
public class DBUtil {
    public static final String KEY_ID = "_id";

    /**
     * 根据实体类生成创建数据库表sql语句
     *
     * @param tableClass
     * @return
     */
    public static String getCreateTableSql(Class<?> tableClass) {
        String sql = "";
        List<FieldBean> fieldList = XCBeanUtil.getFieldList(tableClass);
        if (fieldList != null && !fieldList.isEmpty()) {
            for (FieldBean entity : fieldList) {
                String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                if (!name.equals(DBUtil.KEY_ID)) {
                    if (entity.isUnique()) {
                        sql += "," + name + " text unique";
                    } else {
                        sql += "," + name + " text";
                    }
                }
            }
        }
        return "create table if not exists " + tableClass.getSimpleName() + "(" + DBUtil.KEY_ID + " integer not null primary key autoincrement" + sql + ")";
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/5/22 15:28
     * Description：获取插入sql语句
     */
    public static <T> String getInsertSql(List<T> classObjectList, T conditionObject) {
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
                insertSql = "insert or replace into " + classObjectList.get(0).getClass().getSimpleName() + " " + key + " select " + value;
                querySql = " where not exists ( " + getQuerySql(conditionObject, -1, -1, null, null, null, null) + " )";
            } else {
                insertSql = "insert or replace into " + classObjectList.get(0).getClass().getSimpleName() + " " + key + " values " + value;
            }
            return insertSql + querySql;
        }
        return "";
    }

    /**
     * 获取key与value的sql语句
     *
     * @param classObject 类对象
     * @return key, key与value, value
     */
    public static String[] getKeyAndValueSql(Object classObject) {
        String key = "";
        String value = "";
        List<FieldBean> fieldList = XCBeanUtil.getFieldList(classObject.getClass());
        if (fieldList != null && !fieldList.isEmpty()) {
            for (FieldBean entity : fieldList) {
                String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                if (!name.equals(DBUtil.KEY_ID)) {
                    // key
                    key += name + ",";
                    // value
                    String tempValue = "" + XCBeanUtil.invokeGetMethod(classObject, original);
                    String getValue = !XCStringUtil.isEmpty(tempValue) ? tempValue : "";
                    value += "'" + getValue + "',";
                }
            }
            key = key.substring(0, key.length() - 1);
            value = value.substring(0, value.length() - 1);
        }
        return new String[]{key, value};
    }

    /**
     * 根据类对象生成-查询sql语句
     *
     * @param classObject 类对象
     * @param limit       分页查询-获取数量
     * @param offset      分页查询-起始索引(从0开始)
     * @param field       模糊/日期查询-字段名
     * @param like        模糊查询-包含字符串
     * @param startDate   日期查询-起始日期(日期格式)
     * @param endDate     日期查询-结束日期(日期格式)
     * @return
     */
    public static String getQuerySql(Object classObject, int limit, int offset, String field, String like, String startDate, String endDate) {
        String pageSql = "";
        if (limit >= 0 && offset >= 0) {//分页查询
            pageSql = " limit " + limit + " offset " + offset;
        }
        if (!XCStringUtil.isEmpty(field) && !XCStringUtil.isEmpty(like)) {//模糊查询
            return "select * from " + classObject.getClass().getSimpleName() + " where " + field + " like '%" + like + "%'" + pageSql;
        } else if (!XCStringUtil.isEmpty(field) && !XCStringUtil.isEmpty(startDate) && !XCStringUtil.isEmpty(endDate)) {//日期查询
            return "select * from " + classObject.getClass().getSimpleName() + " where " + field + ">='" + startDate + "' and " + field + "<='" + endDate + "'" + pageSql;
        } else {//条件查询
            String condition = getKeyEqualValueSql(classObject, "and", false);
            if (XCStringUtil.isEmpty(condition)) {
                return "select * from " + classObject.getClass().getSimpleName() + pageSql;
            } else {
                return "select * from " + classObject.getClass().getSimpleName() + " where " + condition + pageSql;
            }
        }
    }

    /**
     * 获取key等于value的sql语句
     *
     * @param classObject 类对象
     * @param connectFlag ","或"and"
     * @return key = 'value' connectFlag key = 'value'
     */
    public static String getKeyEqualValueSql(Object classObject, String connectFlag, boolean isUpdate) {
        String condition = "";
        List<FieldBean> fieldList = XCBeanUtil.getFieldList(classObject.getClass());
        if (fieldList != null && !fieldList.isEmpty()) {
            for (FieldBean entity : fieldList) {
                String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                // key
                String key = name;
                // value
                String tempValue = "" + XCBeanUtil.invokeGetMethod(classObject, original);
                String value = !XCStringUtil.isEmpty(tempValue) ? tempValue : "";
                if (!XCStringUtil.isEmpty(value) || isUpdate) {
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
     * Author：ZhangXuanChen
     * Time：2020/5/23 9:15
     * Description：获取删除sql语句
     */
    public static <T> String getDeleteSql(String field, List<T> classObjectList) {
        if (!XCStringUtil.isEmpty(field) && classObjectList != null && !classObjectList.isEmpty()) {
            String key = "";
            String value = "";
            for (int i = 0; i < classObjectList.size(); i++) {
                List<FieldBean> fieldList = XCBeanUtil.getFieldList(classObjectList.get(i).getClass());
                if (fieldList != null && !fieldList.isEmpty()) {
                    for (FieldBean entity : fieldList) {
                        String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                        String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                        //优先采用别名，无别名再采用原名
                        String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                        if (field.equals(original) || field.equals(alias)) {
                            // key
                            key = name;
                            String tempVal = "" + XCBeanUtil.invokeGetMethod(classObjectList.get(i), original);
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
    public static String getUpdateSql(Object updateObject, Object conditionObject) {
        String updateSql = DBUtil.getKeyEqualValueSql(updateObject, ",", true);
        String conditionSql = DBUtil.getKeyEqualValueSql(conditionObject, "and", false);
        if (XCStringUtil.isEmpty(conditionSql)) {
            return "update " + conditionObject.getClass().getSimpleName() + " set " + updateSql;
        } else {
            return "update " + conditionObject.getClass().getSimpleName() + " set " + updateSql + " where " + conditionSql;
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
    public static String getQueryJoinSql(String joinType, Class<?> masterClass, String masterField, Map<Class<?>, String> slaveMap) {
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
     * Param：masterClass 主类
     * Param：slaveObjectMap 副类集合
     */
    public static String getJoinFieldAliasSql(Class<?> joinClass) {
        String condition = joinClass.getSimpleName() + "." + DBUtil.KEY_ID + " as " + joinClass.getSimpleName() + DBUtil.KEY_ID + ",";
        List<FieldBean> fieldList = XCBeanUtil.getFieldList(joinClass);
        if (fieldList != null && !fieldList.isEmpty()) {
            for (FieldBean entity : fieldList) {
                String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(alias) ? alias : original;
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
     * Author：ZhangXuanChen
     * Time：2020/8/18 16:24
     * Description：parseClassObject
     */
    public static <T> T parseClassObject(Cursor cursor, T classObject, boolean isAlias) {
        try {
            T newClassObject = (T) classObject.getClass().newInstance();
            List<FieldBean> fieldList = XCBeanUtil.getFieldList(classObject.getClass());
            if (fieldList != null && !fieldList.isEmpty()) {
                for (FieldBean entity : fieldList) {
                    String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                    String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                    //优先采用别名，无别名再采用原名
                    String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                    // key
                    String key;
                    if (isAlias) {
                        key = classObject.getClass().getSimpleName() + name;
                    } else if (entity.isKeyId()) {
                        key = DBUtil.KEY_ID;
                    } else {
                        key = name;
                    }
                    // value
                    String value = cursor.getString(cursor.getColumnIndex(key));
                    XCBeanUtil.invokeSetMethod(newClassObject, original, value);
                }
            }
            return newClassObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/4/12 10:11
     * Description：修改表结构-获取导入sql语句
     */
    public static String getImportSql(SQLiteDatabase db, String oldTableName, Class<?> tableClass) {
        Cursor c = null;
        String[] oldData = null;
        try {
            c = db.rawQuery("select * from " + oldTableName + " where 0", null);
            oldData = c.getColumnNames();//oldTable
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        //
        List<FieldBean> newList = XCBeanUtil.getFieldList(tableClass);//newTable
        if (oldData != null && oldData.length > 0 && newList != null && !newList.isEmpty()) {
            List<String> fieldList = new ArrayList<String>();
            for (String old : oldData) {
                for (FieldBean entity : newList) {
                    String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                    String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                    //优先采用别名，无别名再采用原名
                    String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                    // key
                    String key = name;
                    if (!old.equals(KEY_ID) && !key.equals(KEY_ID) && old.equals(key)) {
                        fieldList.add(key);
                    }
                }
            }
            //
            if (fieldList != null && !fieldList.isEmpty()) {
                String sql = "";
                for (String str : fieldList) {
                    sql += str + ",";
                }
                sql = sql.substring(0, sql.length() - 1);
                return "insert into " + tableClass.getSimpleName() + "(" + sql + ") select " + sql + " from " + oldTableName;
            }
        }
        return "";
    }

}
