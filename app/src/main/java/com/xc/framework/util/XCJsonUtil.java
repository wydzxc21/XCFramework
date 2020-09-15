package com.xc.framework.util;

import com.xc.framework.bean.FieldBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangXuanChen
 * @date 2015/11/18
 * @package com.xc.framework.utils
 * @description json解析工具类-bean变量名需与json字段名相同
 */
public class XCJsonUtil {
	// ------------------------------------------解析单独字段-----------------------------------------

	/**
	 * 解析单独字段
	 *
	 * @param jsonResult json结果
	 * @param fieldName  字段名
	 * @return 字段值
	 */
	public static String parseField(String jsonResult, String fieldName) {
		return parseField(jsonResult, null, fieldName);
	}

	/**
	 * 解析单独字段
	 *
	 * @param jsonResult     json结果
	 * @param fieldName      字段名
	 * @param jsonObjectName jsonObject节点名
	 * @return 字段值
	 */
	public static String parseField(String jsonResult, String jsonObjectName, String fieldName) {
		if (!XCStringUtil.isEmpty(jsonResult)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonResult);
				return parseField(jsonObject, jsonObjectName, fieldName);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 解析单独字段
	 *
	 * @param jsonObject jsonObject对象
	 * @param fieldName  字段名
	 * @return 字段值
	 */
	public static String parseField(JSONObject jsonObject, String fieldName) {
		return parseField(jsonObject, null, fieldName);
	}

	/**
	 * 解析单独字段
	 *
	 * @param jsonObject     jsonObject对象
	 * @param fieldName      字段名
	 * @param jsonObjectName 节点名
	 * @return 字段值
	 */
	public static String parseField(JSONObject jsonObject, String jsonObjectName, String fieldName) {
		String str = "";
		if (jsonObject != null && !XCStringUtil.isEmpty(fieldName)) {
			try {
				if (!XCStringUtil.isEmpty(jsonObjectName) && !jsonObject.isNull(jsonObjectName)) {
					jsonObject = jsonObject.optJSONObject(jsonObjectName);
				}
				str = jsonObject.optString(fieldName, "");
			} catch (Exception e) {
			}
		}
		return !XCStringUtil.isEmpty(str) ? str : "";
	}

	// ----------------------------------------------解析Bean---------------------------------------------

	/**
	 * 解析bean
	 *
	 * @param jsonResult  json结果
	 * @param objectClass 实体类
	 * @return 结果bean
	 */
	public static <T> T parseBean(String jsonResult, Class<T> objectClass) {
		return parseBean(jsonResult, null, objectClass);
	}

	/**
	 * 解析bean
	 *
	 * @param jsonResult     json结果
	 * @param jsonObjectName jsonObject节点名
	 * @param objectClass    实体类
	 * @return 结果bean
	 */
	public static <T> T parseBean(String jsonResult, String jsonObjectName, Class<T> objectClass) {
		if (!XCStringUtil.isEmpty(jsonResult)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonResult);
				return parseBean(jsonObject, jsonObjectName, objectClass);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 解析bean
	 *
	 * @param jsonObject  jsonObject对象
	 * @param objectClass 实体类
	 * @return 结果bean
	 */
	public static <T> T parseBean(JSONObject jsonObject, Class<T> objectClass) {
		return parseBean(jsonObject, null, objectClass);
	}

	/**
	 * 解析bean
	 *
	 * @param jsonObject     jsonObject对象
	 * @param jsonObjectName jsonObject节点名
	 * @param objectClass    实体类
	 * @return 结果bean
	 */
	public static <T> T parseBean(JSONObject jsonObject, String jsonObjectName, Class<T> objectClass) {
		T info = null;
		try {
			if (jsonObject != null && objectClass != null) {
				info = (T) objectClass.newInstance();
				if (info != null) {
					if (!XCStringUtil.isEmpty(jsonObjectName) && !jsonObject.isNull(jsonObjectName)) {
						jsonObject = jsonObject.optJSONObject(jsonObjectName);
					}
					List<FieldBean> fieldList = XCBeanUtil.getFieldList(objectClass);
					if (fieldList != null && !fieldList.isEmpty()) {
						for (FieldBean entity : fieldList) {
							String original = !XCStringUtil.isEmpty(entity.getOriginal()) ? entity.getOriginal() : "";
							String alias = !XCStringUtil.isEmpty(entity.getAlias()) ? entity.getAlias() : "";
							//优先采用别名，无别名再采用原名
							String name = !XCStringUtil.isEmpty(alias) ? alias : original;
							if (!XCStringUtil.isEmpty(name) && !jsonObject.isNull(name)) {
								String value = jsonObject.optString(name, "");
								XCBeanUtil.invokeSetMethod(info, original, !XCStringUtil.isEmpty(value) ? value : "");//赋值给原名
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return info;
	}

	// --------------------------------------------解析ListBean-------------------------------------------

	/**
	 * 解析ListBean
	 *
	 * @param jsonResult    json结果
	 * @param jsonArrayName jsonArray节点名
	 * @param objectClass   实体类
	 * @return 结果集
	 */
	public static <T> List<T> parseListBean(String jsonResult, String jsonArrayName, Class<T> objectClass) {
		if (!XCStringUtil.isEmpty(jsonResult)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonResult);
				return parseListBean(jsonObject, jsonArrayName, objectClass);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 解析ListBean
	 *
	 * @param jsonObject    jsonObject对象
	 * @param jsonArrayName jsonArray节点名
	 * @param objectClass   实体类
	 * @return 结果集
	 */
	public static <T> List<T> parseListBean(JSONObject jsonObject, String jsonArrayName, Class<T> objectClass) {
		return parseListBean(jsonObject, jsonArrayName, null, objectClass);
	}

	/**
	 * 解析循环ListBean
	 *
	 * @param jsonResult    json结果
	 * @param jsonArrayName jsonArray节点名(各个子级数组名必须相同)
	 * @param loopsListName 循环解析的List容器变量名
	 * @param objectClass   实体类
	 * @return 结果集
	 */
	public static <T> List<T> parseListBean(String jsonResult, String jsonArrayName, String loopsListName, Class<T> objectClass) {
		if (!XCStringUtil.isEmpty(jsonResult)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonResult);
				return parseListBean(jsonObject, jsonArrayName, loopsListName, objectClass);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 解析循环ListBean
	 *
	 * @param jsonResult    json结果
	 * @param jsonArrayName jsonArray节点名(各个子级数组名)
	 * @param loopsListName 循环解析的List容器变量名
	 * @param objectClass   实体类
	 * @return 结果集
	 */
	public static <T> List<T> parseListBean(String jsonResult, String[] jsonArrayName, String loopsListName, Class<T> objectClass) {
		if (!XCStringUtil.isEmpty(jsonResult)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonResult);
				return parseListBean(jsonObject, jsonArrayName, loopsListName, objectClass);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 解析循环ListBean
	 *
	 * @param jsonObject    jsonObject对象
	 * @param jsonArrayName jsonArray节点名(各个子级数组名)
	 * @param loopsListName 循环解析的List容器变量名
	 * @param objectClass   实体类
	 * @return 结果集
	 */
	public static <T> List<T> parseListBean(JSONObject jsonObject, String[] jsonArrayName, String loopsListName, Class<T> objectClass) {
		return parseListBean(jsonObject, jsonArrayName, loopsListName, 0, objectClass);
	}

	/**
	 * 解析循环ListBean
	 *
	 * @param jsonObject    jsonObject对象
	 * @param jsonArrayName jsonArray节点名(各个子级数组名)
	 * @param loopsListName 循环解析的List容器变量名
	 * @param objectClass   实体类
	 * @param position      递归变量,必须传0
	 * @return 结果集
	 */
	private static <T> List<T> parseListBean(JSONObject jsonObject, String[] jsonArrayName, String loopsListName, int position, Class<T> objectClass) {
		List<T> mList = new ArrayList<T>();
		if (jsonObject != null && jsonArrayName != null && jsonArrayName.length > 0 && objectClass != null) {
			try {
				if (!jsonObject.isNull(jsonArrayName[position]) && position < jsonArrayName.length) {
					JSONArray array = jsonObject.optJSONArray(jsonArrayName[position]);
					if (array != null && array.length() > 0) {
						position++;
						for (int i = 0; i < array.length(); i++) {
							JSONObject json = array.optJSONObject(i);
							if (json != null) {
								T info = parseBean(json, objectClass);
								if (info != null) {
									if (!XCStringUtil.isEmpty(loopsListName) && position < jsonArrayName.length) {
										List<T> parseListBean = parseListBean(json, jsonArrayName, loopsListName, position, objectClass);
										if (parseListBean != null) {
											XCBeanUtil.invokeSetMethod(info, loopsListName, parseListBean);
										}
									}
									mList.add(info);
								}
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return mList;
	}

	/**
	 * 解析循环ListBean
	 *
	 * @param jsonObject    jsonObject对象
	 * @param jsonArrayName jsonArray节点名(各个子级数组名必须相同)
	 * @param loopsListName 循环解析的List容器变量名
	 * @param objectClass   实体类
	 * @return 结果集
	 */
	public static <T> List<T> parseListBean(JSONObject jsonObject, String jsonArrayName, String loopsListName, Class<T> objectClass) {
		List<T> mList = new ArrayList<T>();
		if (jsonObject != null && !XCStringUtil.isEmpty(jsonArrayName) && objectClass != null) {
			try {
				if (!jsonObject.isNull(jsonArrayName)) {
					JSONArray array = jsonObject.optJSONArray(jsonArrayName);
					if (array != null && array.length() > 0) {
						for (int i = 0; i < array.length(); i++) {
							JSONObject json = array.optJSONObject(i);
							if (json != null) {
								T info = parseBean(json, objectClass);
								if (info != null) {
									if (!XCStringUtil.isEmpty(loopsListName)) {
										List<T> parseListBean = parseListBean(json, jsonArrayName, loopsListName, objectClass);
										if (parseListBean != null) {
											XCBeanUtil.invokeSetMethod(info, loopsListName, parseListBean);
										}
									}
									mList.add(info);
								}
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return mList;
	}

	// ------------------------------------------解析ListArray----------------------------------------

	/**
	 * 解析ListArray
	 *
	 * @param jsonResult    json结果
	 * @param jsonArrayName jsonArray节点名
	 * @return 结果集
	 */
	public static List<String> parseListArray(String jsonResult, String jsonArrayName) {
		if (!XCStringUtil.isEmpty(jsonResult)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonResult);
				return parseListArray(jsonObject, jsonArrayName);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 解析ListArray
	 *
	 * @param jsonObject    jsonObject对象
	 * @param jsonArrayName jsonArray节点名
	 * @return 结果集
	 */
	public static List<String> parseListArray(JSONObject jsonObject, String jsonArrayName) {
		List<String> mList = new ArrayList<String>();
		if (jsonObject != null && !XCStringUtil.isEmpty(jsonArrayName)) {
			try {
				if (!jsonObject.isNull(jsonArrayName)) {
					JSONArray array = jsonObject.optJSONArray(jsonArrayName);
					if (array != null && array.length() > 0) {
						for (int i = 0; i < array.length(); i++) {
							String str = array.optString(i, "");
							if (!XCStringUtil.isEmpty(str)) {
								mList.add(str);
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return mList;
	}
}
