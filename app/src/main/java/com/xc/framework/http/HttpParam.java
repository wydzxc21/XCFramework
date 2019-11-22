package com.xc.framework.http;

import android.util.Log;

import com.xc.framework.http.client.HttpThread;
import com.xc.framework.http.interfaces.ParamInterface;
import com.xc.framework.http.multipart.content.ContentBody;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhangXuanChen
 * @date 2015-9-9
 * @package com.frame.net
 * @description 网络参数基类
 */
public class HttpParam implements ParamInterface {
    private static final String GET = "get";
    private static final String POST = "post";
    private String url;// 请求url
    private HashMap<String, BasicNameValuePair> params;// getpost参数集合
    private HashMap<String, ContentBody> contentBodyParams;// 对象参数集合
    private HashMap<String, Object> jsonParams;//json参数集合
    private String saveFilePath;// 保存下载文件绝对路径
    //
    private String threadName;//线程名

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(HttpThread httpThread) {
        this.threadName = httpThread.getName();
    }


    /**
     * 请求url地址
     *
     * @param url
     * @description 同时添加get和post参时采用post请求, get参拼在url后
     */
    public HttpParam(String url) {
        this.url = url;
        params = new LinkedHashMap<String, BasicNameValuePair>();
        contentBodyParams = new LinkedHashMap<String, ContentBody>();
        jsonParams = new LinkedHashMap<String, Object>();
    }

    /**
     * 添加json参数
     *
     * @param key    key
     * @param object json对象
     */
    public void addJsonParam(String key, Object object) {
        jsonParams.put(key, object);
    }

    /**
     * 添加Get参数
     *
     * @param key   参数名
     * @param value 参数值
     * @description 同时添加get和post参时采用post请求, get参拼在url后
     */
    public void addGetParam(String key, String value) {
        params.put(key, new BasicNameValuePair(GET, value));
    }

    /**
     * 添加Post参数
     *
     * @param key   参数名
     * @param value 参数值
     * @description 同时添加get和post参时采用post请求, get参拼在url后
     */
    public void addPostParam(String key, String value) {
        params.put(key, new BasicNameValuePair(POST, value));
    }

    /**
     * 添加contentBody对象
     *
     * @param key         参数名
     * @param contentBody 对象
     * @description post上传, 同时可调用addPostParam()方法, 添加post参数
     */
    public void addContentBodyParam(String key, ContentBody contentBody) {
        contentBodyParams.put(key, contentBody);
    }

    /**
     * 设置保存下载文件的绝对路径
     *
     * @param saveFilePath 例:/storage/emulated/0/folder/test.txt
     */
    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }


    /**
     * 获取json内容
     *
     * @return
     */
    public String getJsonContent() {
        try {
            if (jsonParams != null && !jsonParams.isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                for (ConcurrentHashMap.Entry<String, Object> entry : jsonParams.entrySet()) {
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
                return jsonObject.toString().trim();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 清除上传参数集
     */
    public void clearUploadParams() {
        if (contentBodyParams != null && !contentBodyParams.isEmpty()) {
            contentBodyParams.clear();
        }
    }

    /**
     * 清除上传参数集
     */
    public void clearDownloadParams() {
        saveFilePath = null;
    }

    /**
     * 获取url名
     *
     * @return
     */
    public String getUrlName() {
        return url;
    }

    /**
     * 获取Url
     *
     * @return
     */
    @Override
    public String getURL() {
        return getHaveParamUrl(getGetList());
    }

    /**
     * 设置Url
     *
     * @param url
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     * 控制台输出
     */
    @Override
    public void outURL(String tagName) {
        Log.i(tagName, "Url：" + getURL());
        if (getPostList() != null && !getPostList().isEmpty()) {
            Log.i(tagName, "PostParam：" + getPostList().toString());
        } else if (getGetList() != null && !getGetList().isEmpty()) {
            Log.i(tagName, "GetParam：" + getGetList().toString());
        }
    }

    /**
     * 获取post参数集合
     *
     * @return
     */
    public ArrayList<BasicNameValuePair> getPostList() {
        return getParamList(POST);
    }

    /**
     * 获取get参数集合
     *
     * @return
     */
    public ArrayList<BasicNameValuePair> getGetList() {
        return getParamList(GET);
    }

    /**
     * 获取contentBody参数集合
     *
     * @return
     */
    public HashMap<String, ContentBody> getContentBodyMap() {
        return contentBodyParams;
    }

    /**
     * 获取json参数集合
     *
     * @return
     */
    public HashMap<String, Object> getJsonMap() {
        return jsonParams;
    }

    /**
     * 设置保存下载文件的绝对路径
     *
     * @return saveFilePath 例:/storage/emulated/0/folder/test.txt
     */
    public String getSaveFilePath() {
        return saveFilePath;
    }

    /**
     * 获取参数集合
     *
     * @param mode
     * @return
     */
    private ArrayList<BasicNameValuePair> getParamList(String mode) {
        ArrayList<BasicNameValuePair> mList = new ArrayList<BasicNameValuePair>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, BasicNameValuePair> entry : params.entrySet()) {
                BasicNameValuePair value = entry.getValue();
                if (value != null) {
                    if (mode.equals(value.getName() != null ? value.getName() : "")) {
                        BasicNameValuePair info = new BasicNameValuePair(entry.getKey() != null ? entry.getKey() : "", value.getValue() != null ? value.getValue() : "");
                        mList.add(info);
                    }
                }
            }
        }
        return mList;
    }

    /**
     * 获取带参数的url
     *
     * @return
     */
    private String getHaveParamUrl(List<BasicNameValuePair> getList) {
        if (getList != null && !getList.isEmpty()) {// get|post
            String param = "";
            for (BasicNameValuePair bnvp : getList) {
                if (bnvp != null) {
                    String temp = bnvp.getName() + "=" + bnvp.getValue();
                    if ("".equals(param)) {
                        param += temp;
                    } else {
                        param += "&" + temp;
                    }
                }
            }
            if (!url.contains("?")) {
                return url + "?" + param;
            } else {
                return url + "&" + param;
            }
        }
        return url;// post
    }
}
