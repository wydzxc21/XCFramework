package com.xc.framework.http.interfaces;

/**
 * @author ZhangXuanChen
 * @date 2015-9-9
 * @package com.anhry.android.utils.net.urlmode.param
 * @description 网络参数行为接口
 */
public interface ParamInterface {
	/**
	 * 获取URL
	 * @return
	 */
	public String getURL();

	/**
	 * 控制台打印URL
	 */
	public void outURL(String tagName);
}
