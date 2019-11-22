package com.xc.framework.http.interfaces;

/**
 * @author ZhangXuanChen
 * @date 2015-10-13
 * @package com.xc.framework.https.multipart
 * @description 上传回调接口
 */
public interface UploadCallBack {
	/**
	 * 上传中
	 * 
	 * @param total
	 *            总文件大小
	 * @param current
	 *            当前上传大小
	 * @return
	 */
	public void onLoading(long total, long current);

	/**
	 * 上传结果
	 * 
	 * @param response
	 */
	public void onResult(String response);

}
