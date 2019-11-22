package com.xc.framework.bitmap;

import android.content.Context;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xc.framework.bitmap.core.BitmapSize;

import java.io.File;
import java.lang.reflect.Field;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap
 * @description
 */
public class BitmapOtherUtil {

	private BitmapOtherUtil() {
	}

	public static long getAvailableSpace(File dir) {
		try {
			final StatFs stats = new StatFs(dir.getPath());
			return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
		} catch (Throwable e) {
			Log.e("getAvailableSpace", e.getMessage(), e);
			return -1;
		}
	}

	private static SSLSocketFactory sslSocketFactory;

	public static void trustAllHttpsURLConnection() {
		// Create a trust manager that does not validate certificate chains
		if (sslSocketFactory == null) {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			try {
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, trustAllCerts, null);
				sslSocketFactory = sslContext.getSocketFactory();
			} catch (Throwable e) {
				Log.e("trustAllHttpsURLConnection", e.getMessage(), e);
			}
		}

		if (sslSocketFactory != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
			HttpsURLConnection.setDefaultHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		}
	}

	private static BitmapSize screenSize = null;

	public static BitmapSize getScreenSize(Context context) {
		if (screenSize == null) {
			DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
			screenSize = new BitmapSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
		}
		return screenSize;
	}

	public static BitmapSize optimizeMaxSizeByView(View view, int maxImageWidth, int maxImageHeight) {
		int width = maxImageWidth;
		int height = maxImageHeight;

		if (width > 0 && height > 0) {
			return new BitmapSize(width, height);
		}

		final ViewGroup.LayoutParams params = view.getLayoutParams();
		if (params != null) {
			if (params.width > 0) {
				width = params.width;
			} else if (params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
				width = view.getWidth();
			}

			if (params.height > 0) {
				height = params.height;
			} else if (params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
				height = view.getHeight();
			}
		}

		if (width <= 0)
			width = getImageViewFieldValue(view, "mMaxWidth");
		if (height <= 0)
			height = getImageViewFieldValue(view, "mMaxHeight");

		BitmapSize screenSize = getScreenSize(view.getContext());
		if (width <= 0)
			width = screenSize.getWidth();
		if (height <= 0)
			height = screenSize.getHeight();

		return new BitmapSize(width, height);
	}

	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		if (object instanceof ImageView) {
			try {
				Field field = ImageView.class.getDeclaredField(fieldName);
				field.setAccessible(true);
				int fieldValue = (Integer) field.get(object);
				if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
					value = fieldValue;
				}
			} catch (Throwable e) {
			}
		}
		return value;
	}
}
