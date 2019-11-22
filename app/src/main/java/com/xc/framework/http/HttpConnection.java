package com.xc.framework.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.xc.framework.http.client.ConnectionConfig;
import com.xc.framework.http.client.DefaultSSLSocketFactory;
import com.xc.framework.http.client.GZipDecompressingEntity;
import com.xc.framework.http.client.HttpHandler;
import com.xc.framework.http.client.RetryHandler;
import com.xc.framework.http.interfaces.DownloadCallBack;
import com.xc.framework.http.interfaces.UploadCallBack;
import com.xc.framework.util.XCNetUtil;
import com.xc.framework.util.XCStringUtil;

/**
 * @author ZhangXuanChen
 * @date 2015-9-9
 * @package com.frame.net
 * @description 网络连接类
 */
public class HttpConnection {
	/**
	 * 使用apache的get方式请求网络
	 * 
	 * @param context
	 * @param param
	 *            下载回调
	 * @return
	 */
	public static String httpGet(Context context, HttpParam param) {
		return httpGet(context, param, null, null);
	}

	/**
	 * 使用apache的get方式请求网络
	 * 
	 * @param context
	 * @param param
	 * @param downloadCallBack
	 *            下载回调
	 * @return
	 */
	public static String httpGet(Context context, HttpParam param, DownloadCallBack downloadCallBack) {
		return httpGet(context, param, null, downloadCallBack);
	}

	/**
	 * 使用apache的get方式请求网络
	 * 
	 * @param context
	 * @param config
	 * @return InputStream
	 * @throws Exception
	 */
	public synchronized static String httpGet(Context context, HttpParam param, ConnectionConfig config, DownloadCallBack downloadCallBack) {
		DefaultHttpClient client = new DefaultHttpClient();
		String response = "";
		try {
			setHttpParams(client, config);
			HttpGet httpGet = new HttpGet(param.getURL());
			//
			HttpResponse hr = client.execute(httpGet);
			if (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = hr.getEntity();
				if (entity != null) {
					String value = entity.getContentType().getValue();
					if ("text/html".equals(value) || value.contains("application/json")) {// 数据
						response = EntityUtils.toString(entity, config.getEncode());
					} else {// 文件
						HttpHandler.downloadFile(context, entity, param.getSaveFilePath(), param.getURL(), downloadCallBack);
					}
					entity.consumeContent();
				}
			}else {
				response = "" + hr.getStatusLine().getStatusCode();
			}
		} catch (ClientProtocolException e) {
			Log.e("ClientProtocolException", e.getMessage());
		} catch (ConnectTimeoutException e) {// 连接超时
			Log.e("ConnectTimeoutException", "连接超时" + e.getMessage());
		} catch (InterruptedIOException e) {// 请求超时
			Log.e("InterruptedIOException", "请求超时" + e.getMessage());
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
		} finally {
			client.getConnectionManager().shutdown();
		}
		return response;
	}

	// -----------------------------------------------------------post--------------------------------------------------------
	/**
	 * 使用apache的post方式请求网络
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(HttpParam param) {
		return httpPost(param, null, null);
	}

	/**
	 * 使用apache的post方式请求网络
	 * 
	 * @param param
	 * @param uploadCallBack
	 *            上传回调
	 * @return
	 */
	public static String httpPost(HttpParam param, UploadCallBack uploadCallBack) {
		return httpPost(param, null, uploadCallBack);
	}

	/**
	 * 使用apache的post方式请求网络
	 * 
	 * @param param
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public synchronized static String httpPost(HttpParam param, ConnectionConfig config, UploadCallBack uploadCallBack) {
		DefaultHttpClient client = new DefaultHttpClient();
		String response = "";
		try {
			setHttpParams(client, config);
			HttpPost httpPost = new HttpPost(param.getURL());
			//
	        HttpEntity entity = null;
            if (!XCStringUtil.isEmpty(param.getJsonContent())) {
                entity = new StringEntity(param.getJsonContent(), config.getEncode());
            } else {
                entity = HttpHandler.getEntity(param, config, uploadCallBack);
            }
			if (entity != null) {
				httpPost.setEntity(entity);
				//
				HttpResponse hr = client.execute(httpPost);
				if (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					response = EntityUtils.toString(hr.getEntity(), config.getEncode());
				} else {
					response = "" + hr.getStatusLine().getStatusCode();
				}
				entity.consumeContent();
			}
		} catch (UnsupportedEncodingException e) {
			Log.e("UnsupportedEncodingException", e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e("ClientProtocolException", e.getMessage());
		} catch (ConnectTimeoutException e) {// 连接超时
			Log.e("ConnectTimeoutException", "连接超时" + e.getMessage());
		} catch (InterruptedIOException e) {// 请求超时
			Log.e("InterruptedIOException", "请求超时" + e.getMessage());
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
		} finally {
			client.getConnectionManager().shutdown();
		}
		return response;
	}

	// ---------------------------------------------------------------------------------------------------------------
	/**
	 * 设置http参数
	 * 
	 * @param config
	 * @return
	 */
	private static void setHttpParams(DefaultHttpClient client, ConnectionConfig config) {
		if (config == null) {
			config = new ConnectionConfig();// 默认30秒超时
		}
		//
		HttpParams params = client.getParams();
		if (params != null) {// 设置超时
			// params.setIntParameter(HttpConnectionParams.SO_TIMEOUT,
			// config.getSoTimeout());
			// params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
			// config.getConnectionTimeout());
			//
			ConnManagerParams.setTimeout(params, config.getSoTimeout());
			HttpConnectionParams.setSoTimeout(params, config.getSoTimeout());
			HttpConnectionParams.setConnectionTimeout(params, config.getConnectionTimeout());
			HttpProtocolParams.setUserAgent(params, XCNetUtil.getUserAgent(null));
			ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(10));
			ConnManagerParams.setMaxTotalConnections(params, 10);
			HttpConnectionParams.setTcpNoDelay(params, true);
			HttpConnectionParams.setSocketBufferSize(params, 1024 * 8);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", DefaultSSLSocketFactory.getSocketFactory(), 443));
			client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);

			client.setHttpRequestRetryHandler(new RetryHandler(3));

			client.addRequestInterceptor(new HttpRequestInterceptor() {
				@Override
				public void process(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
					if (!httpRequest.containsHeader("Accept-Encoding")) {
						httpRequest.addHeader("Accept-Encoding", "gzip");
					}
				}
			});

			client.addResponseInterceptor(new HttpResponseInterceptor() {
				@Override
				public void process(HttpResponse response, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
					final HttpEntity entity = response.getEntity();
					if (entity == null) {
						return;
					}
					final Header encoding = entity.getContentEncoding();
					if (encoding != null) {
						for (HeaderElement element : encoding.getElements()) {
							if (element.getName().equalsIgnoreCase("gzip")) {
								response.setEntity(new GZipDecompressingEntity(response.getEntity()));
								return;
							}
						}
					}
				}
			});
		}
	}

}