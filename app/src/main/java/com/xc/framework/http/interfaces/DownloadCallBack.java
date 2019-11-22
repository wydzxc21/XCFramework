package com.xc.framework.http.interfaces;

import java.io.File;

/**
 * @author ZhangXuanChen
 * @date 2015-10-13
 * @package com.xc.framework.https.multipart
 * @description 上传回调接口
 */
public interface DownloadCallBack {
	/**
	 * 下载中
	 * 
	 * @param total
	 *            总文件大小
	 * @param current
	 *            当前上传大小
	 * @return
	 */
	public void onLoading(long total, long current);

	/**
	 * 下载结果
	 * 
	 * @param file
	 */
	public void onResult(File file);

}
