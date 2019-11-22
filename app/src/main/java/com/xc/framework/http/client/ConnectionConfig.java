package com.xc.framework.http.client;

import org.apache.http.protocol.HTTP;


/**
 * @author ZhangXuanChen
 * @date 2015-9-9
 * @package com.frame.net
 * @description 配置连接参数类
 */
public class ConnectionConfig {
	private int soTimeout = 30 * 1000;//请求超时时间
	private int connectionTimeout = 30 * 1000;//连接超时时间
	private String encode = HTTP.UTF_8;//请求编码

	/**
	 * 请求超时时间：默认30s 
	 * 连接超时时间：默认30s 
	 * 请求编码：默认UTF-8
	 */
	public ConnectionConfig() {
	}
	/**
	 * 请求超时时间：默认30s 
	 * 连接超时时间：默认30s 
	 * 请求编码：默认UTF-8
	 */
	public ConnectionConfig(int soTimeout, int connectionTimeout, String encode) {
		this.soTimeout = soTimeout;
		this.connectionTimeout = connectionTimeout;
		this.encode = encode;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
}
