package com.xc.framework.sp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.xc.framework.bean.FieldBean;
import com.xc.framework.util.XCBeanUtil;
import com.xc.framework.util.XCStringUtil;

import java.util.List;

/**
 * @author ZhangXuanChen
 * @date 2020/2/4
 * @package com.xc.framework.sp
 * @description xml管理类
 */
@SuppressLint("ApplySharedPref")
public class SPManager {
    private static SPManager spManager;
    private static String SP_NAME = "xcFramework";
    private SharedPreferences sp;

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description getInstance
     */
    public static SPManager getInstance(Context context) {
        if (spManager == null) {
            spManager = new SPManager(context);
        }
        return spManager;
    }

    /**
     * @param spName xml文件名，text
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 初始化
     */
    public static void init(Context context, String spName) {
        SPManager.SP_NAME = spName;
        if (spManager == null) {
            spManager = new SPManager(context);
        }
    }


    private SPManager(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @author ZhangXuanChen
     * @date 2022/3/11 14:34
     * @description 重命名
     */
    public void rename(String oldKey, String newKey) {
        String value = get(oldKey);
        save(newKey, value);
        remove(oldKey);
    }

    /**
     * @author ZhangXuanChen
     * @date 2022/3/11 15:06
     * @description 重命名
     */
    public <T> void rename(String oldClassName, Class<T> objectClass) {
        List<FieldBean> fieldList = XCBeanUtil.getFieldList(objectClass);
        if (!XCStringUtil.isEmpty(oldClassName) && fieldList != null && !fieldList.isEmpty()) {
            for (FieldBean entity : fieldList) {
                String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                if (!XCStringUtil.isEmpty(name)) {
                    rename(oldClassName + name, objectClass.getSimpleName() + name);
                }
            }
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 保存
     */
    public void save(String key, String value) {
        sp.edit().putString(key, value).commit();
    }

    /**
     * @param classObject 类对象,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 保存
     */
    public <T> void save(T classObject) {
        List<FieldBean> fieldList = XCBeanUtil.getFieldList(classObject.getClass());
        if (fieldList != null && !fieldList.isEmpty()) {
            for (FieldBean entity : fieldList) {
                String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                String value = "" + XCBeanUtil.invokeGetMethod(classObject, original);
                sp.edit().putString(classObject.getClass().getSimpleName() + name, value).commit();
            }
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 获取
     */
    public String get(String key) {
        return get(key, "");
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 获取
     */
    public String get(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    /**
     * @param objectClass 类,只支持String变量
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description 获取
     */
    public <T> T get(Class<T> objectClass) {
        T info = null;
        try {
            info = (T) objectClass.newInstance();
            List<FieldBean> fieldList = XCBeanUtil.getFieldList(objectClass);
            if (fieldList != null && !fieldList.isEmpty()) {
                for (FieldBean entity : fieldList) {
                    String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                    String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                    //优先采用别名，无别名再采用原名
                    String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                    if (!XCStringUtil.isEmpty(name)) {
                        String value = sp.getString(objectClass.getSimpleName() + name, "");
                        XCBeanUtil.invokeSetMethod(info, original, !XCStringUtil.isEmpty(value) ? value : "");//赋值给原名
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/7
     * @description clear
     */
    public <T> void clear(Class<T> objectClass) {
        List<FieldBean> fieldList = XCBeanUtil.getFieldList(objectClass);
        if (fieldList != null && !fieldList.isEmpty()) {
            for (FieldBean entity : fieldList) {
                String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
                String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
                //优先采用别名，无别名再采用原名
                String name = !XCStringUtil.isEmpty(alias) ? alias : original;
                sp.edit().putString(objectClass.getSimpleName() + name, "").commit();
            }
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/4
     * @description clearAll
     */
    public void clearAll() {
        sp.edit().clear().commit();
    }


    /**
     * @author ZhangXuanChen
     * @date 2022/3/11 14:39
     * @description remove
     */
    public void remove(String key) {
        sp.edit().remove(key).commit();
    }
}
