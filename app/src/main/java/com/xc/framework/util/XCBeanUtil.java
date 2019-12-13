package com.xc.framework.util;

import android.util.ArrayMap;

import com.xc.framework.annotation.FieldAlias;
import com.xc.framework.annotation.FieldIgnore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author ZhangXuanChen
 * @date 2015-9-17
 * @package com.xc.framework.utils
 * @description 实体类工具类
 */
public class XCBeanUtil {

	/**
	 * 获取bean成员变量名集合
	 *
	 * @param objectClass 实体类
	 * @return 变量名集合(只含原字段名)
	 */
	public static List<String> getFieldNameList(Class<?> objectClass) {
		List<String> mList = null;
		if (objectClass != null) {
			Map<String, String> fieldNameMap = getFieldNameMap(objectClass);
			if (fieldNameMap != null && !fieldNameMap.isEmpty()) {
				mList = new ArrayList<String>();
				for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
					String name = !XCStringUtil.isEmpty(entry.getKey()) ? entry.getKey() : "";
					mList.add(name);
				}
			}
		}
		return mList;
	}

	/**
	 * 获取bean成员变量名集合
	 *
	 * @param objectClass 实体类
	 * @return 变量名集合（含原字段名、别名）
	 * @deprecated key: 原名 value: 别名
	 */
	public static Map<String, String> getFieldNameMap(Class<?> objectClass) {
		Map<String, String> mMap = null;
		if (objectClass != null) {
			try {
				mMap = new ArrayMap<String, String>();
				Class<?> tempClass = objectClass;
				while (tempClass != null) {
					Field[] fields = tempClass.getDeclaredFields();
					if (fields != null && fields.length > 0) {
						for (int i = 0; i < fields.length; i++) {
							Field field = fields[i];
							if (field != null) {
								String filter = "serialVersionUID";// 序列化变量
								String change = "$";// studio 2.0以上反射多出参数:$change
								String name = field.getName() != null ? field.getName() : "";
								if (!name.equals(filter) && !name.contains(change)) {
									boolean isFieldIgnore = field.isAnnotationPresent(FieldIgnore.class);//忽略字段
									if (!isFieldIgnore) {
										FieldAlias alias = field.getAnnotation(FieldAlias.class);
										mMap.put(name, alias != null ? !XCStringUtil.isEmpty(alias.value()) ? alias.value() : "" : "");
									}
								}
							}
						}
					}
					//
					tempClass = tempClass.getSuperclass();//递归父类
				}
			} catch (Exception e) {
			}
		}
		return mMap;
	}

	/**
	 * 获取get方法
	 *
	 * @param objectClass 实体类
	 * @param fieldName   变量名(驼峰)
	 * @return get方法对象
	 */
	public static Method getGetMethod(Class<?> objectClass, String fieldName) {
		try {
			String newFieldName = "get" + fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
			if (fieldName.length() >= 2) {
				if (Character.isUpperCase(fieldName.charAt(1))) {
					newFieldName = "get" + fieldName;
				}
			}
			Method method = objectClass.getMethod(newFieldName, new Class[]{});
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取set方法
	 *
	 * @param objectClass 实体类
	 * @param fieldName   变量名(驼峰)
	 * @return set方法对象
	 */
	public static Method getSetMethod(Class<?> objectClass, String fieldName) {
		try {
			Field field = objectClass.getDeclaredField(fieldName);
			String newFieldName = "set" + fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
			if (fieldName.length() >= 2) {
				if (Character.isUpperCase(fieldName.charAt(1))) {
					newFieldName = "set" + fieldName;
				}
			}
			Method method = objectClass.getMethod(newFieldName, new Class[]{field.getType()});
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 调用对应成员变量的get方法
	 *
	 * @param classObject 实体类对象
	 * @param fieldName   变量名(驼峰)
	 * @return 是否成功
	 */
	public static Object invokeGetMethod(Object classObject, String fieldName) {
		if (classObject != null && !XCStringUtil.isEmpty(fieldName)) {
			try {
				Class<?> tempClass = classObject.getClass();
				Object object = getGetMethod(tempClass, fieldName).invoke(classObject);
				while (object == null) {
					tempClass = tempClass.getSuperclass();//递归父类
					object = getGetMethod(tempClass, fieldName).invoke(classObject);
				}
				return object;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 调用对应成员变量的set方法
	 *
	 * @param classObject 实体类对象
	 * @param fieldName   变量名(驼峰)
	 * @param setValue    设置值
	 * @return 是否成功
	 */
	public static boolean invokeSetMethod(Object classObject, String fieldName, Object setValue) {
		if (classObject != null && !XCStringUtil.isEmpty(fieldName) && setValue != null) {
			try {
				Class<?> tempClass = classObject.getClass();
				Method method = getSetMethod(tempClass, fieldName);
				while (method == null) {
					tempClass = tempClass.getSuperclass();//递归父类
					method = getSetMethod(tempClass, fieldName);
				}
				method.invoke(classObject, setValue);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
}
