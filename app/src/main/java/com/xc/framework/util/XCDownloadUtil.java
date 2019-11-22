package com.xc.framework.util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

/**
 * @author ZhangXuanChen
 * @date 2017-10-30
 * @package com.xc.framework.utils
 * @description 系统下载工具
 */
public class XCDownloadUtil {

	/**
	 * 开始下载
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            下载地址
	 * @param title
	 *            提示标题
	 * @param description
	 *            提示描述
	 * @param description
	 *            保存本地文件夹名称
	 * @param fileName
	 *            保存本地APK名称(包括扩展名)
	 * @return 下载唯一id
	 */
	@SuppressLint("NewApi")
	public static long startDownload(Context context, String url, String title, String description, String fileName) {
		if (context != null) {
			try {
				DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
				if (mDownloadManager != null) {
					Uri uri = Uri.parse(!XCStringUtil.isEmpty(url) ? url : "");
					if (uri != null) {
						DownloadManager.Request mRequest = new DownloadManager.Request(uri);
						if (mRequest != null) {
							mRequest.setDestinationUri(uri);
							mRequest.setTitle(!XCStringUtil.isEmpty(title) ? title : "");
							mRequest.setDescription(!XCStringUtil.isEmpty(description) ? description : "");
							mRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, !XCStringUtil.isEmpty(fileName) ? fileName : "");
							mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
							mRequest.setMimeType("application/vnd.android.package-archive");
							mRequest.allowScanningByMediaScanner(); // 设置为可被媒体扫描器找到
							mRequest.setVisibleInDownloadsUi(true); // 设置为可见和可管理
							return mDownloadManager.enqueue(mRequest);
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return 0;
	}
}
