package com.xc.framework.http.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.xc.framework.http.HttpParam;
import com.xc.framework.http.interfaces.DownloadCallBack;
import com.xc.framework.http.interfaces.UploadCallBack;
import com.xc.framework.http.multipart.HttpMultipartMode;
import com.xc.framework.http.multipart.MultipartEntity;
import com.xc.framework.http.multipart.content.ContentBody;
import com.xc.framework.http.multipart.content.StringBody;
import com.xc.framework.util.XCFileUtil;
import com.xc.framework.util.XCIOUtil;
import com.xc.framework.util.XCStringUtil;

/**
 * @author ZhangXuanChen
 * @date 2015-10-14
 * @package com.xc.framework.https
 * @description http处理器
 */
public class HttpHandler {
	/**
	 * 获取HttpEntity
	 * 
	 * @param param
	 * @param config
	 * @return
	 */
	public static HttpEntity getEntity(HttpParam param, ConnectionConfig config, UploadCallBack uploadCallBack) {
		HttpEntity result = null;
		try {
			if (param.getContentBodyMap() != null && !param.getContentBodyMap().isEmpty()) {
				MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT, null, Charset.forName(config.getEncode()));
				// post参数
				if (param.getPostList() != null && !param.getPostList().isEmpty()) {
					for (BasicNameValuePair entry : param.getPostList()) {
						multipartEntity.addPart(entry.getName(), new StringBody(entry.getValue()));
					}
				}
				// 上传对象
				for (ConcurrentHashMap.Entry<String, ContentBody> entry : param.getContentBodyMap().entrySet()) {
					multipartEntity.addPart(entry.getKey(), entry.getValue());
				}
				// 上传进度
				if (uploadCallBack != null) {
					multipartEntity.setUploadCallBack(uploadCallBack);
				}
				result = multipartEntity;
			} else if (param.getPostList() != null && !param.getPostList().isEmpty()) {// 无上传对象
				result = new UrlEncodedFormEntity(param.getPostList(), config.getEncode());
			}
		} catch (Exception e) {
			Log.e("" + e.getMessage(), "" + e);
		}
		return result;
	}

	/**
	 * 下载文件
	 * 
	 * @param inputStream
	 * @param savePath
	 */
	public static void downloadFile(Context context, HttpEntity entity, String savePath, String downloadUrl, DownloadCallBack downloadCallBack) {
		File targetFile = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			if (entity != null) {
				if (XCStringUtil.isEmpty(savePath)) {
					savePath = XCFileUtil.getDiskCacheDir(context) + File.separator + XCFileUtil.getDownloadFileName(downloadUrl);
				}
				//
				targetFile = new File(savePath);
				//
				if (targetFile.exists()) {
					targetFile.delete();
				}
				File dir = targetFile.getParentFile();
				if (dir.exists() || dir.mkdirs()) {
					targetFile.createNewFile();
				}
				//
				long current = 0;
				long total = entity.getContentLength() + current;
				//
				FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
				bis = new BufferedInputStream(entity.getContent());
				bos = new BufferedOutputStream(fileOutputStream);
				//
				byte[] tmp = new byte[4096];
				int len;
				while ((len = bis.read(tmp)) != -1) {
					bos.write(tmp, 0, len);
					current += len;
					if (downloadCallBack != null) {
						downloadCallBack.onLoading(total, current);
					}
				}
				bos.flush();
				//
				if (downloadCallBack != null) {
					downloadCallBack.onResult(targetFile);
				}
			}
		} catch (Exception e) {
		} finally {
			XCIOUtil.close(bis);
			XCIOUtil.close(bos);
		}
	}

}
