package com.xc.framework.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.view.View;

import com.xc.framework.bitmap.BitmapDisplayConfig;
import com.xc.framework.bitmap.BitmapLoader;
import com.xc.framework.bitmap.callback.BitmapLoadCallBack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.example.androiddemo
 * @description 图片异步加载工具
 */
public class XCBitmapUtil {
	// --------------------------------------------------------------------显示----------------------------------------------------------------
	/**
	 * 显示view图片
	 *
	 * @param context
	 *            上下文
	 * @param container
	 *            容器
	 * @param url
	 *            地址
	 */
	public static <T extends View> void display(Context context, T container, String url) {
		BitmapLoader.getInstance(context).display(container, url, null, null);
	}

	/**
	 * 显示view图片
	 *
	 * @param context
	 *            上下文
	 * @param container
	 *            容器
	 * @param url
	 *            地址
	 * @param diskCachePath
	 *            本地缓存路径
	 */
	public static <T extends View> void display(Context context, T container, String url, String diskCachePath) {
		BitmapLoader.getInstance(context, diskCachePath).display(container, url, null, null);
	}

	/**
	 * 显示view图片
	 *
	 * @param context
	 *            上下文
	 * @param container
	 *            容器
	 * @param url
	 *            地址
	 * @param callBack
	 *            加载回调
	 */
	public static <T extends View> void display(Context context, T container, String url, BitmapLoadCallBack<T> callBack) {
		BitmapLoader.getInstance(context).display(container, url, null, callBack);
	}

	/**
	 * 显示view图片
	 *
	 * @param context
	 *            上下文
	 * @param container
	 *            容器
	 * @param url
	 *            地址
	 * @param diskCachePath
	 *            本地缓存路径
	 * @param callBack
	 *            加载回调
	 */
	public static <T extends View> void display(Context context, T container, String url, String diskCachePath, BitmapLoadCallBack<T> callBack) {
		BitmapLoader.getInstance(context, diskCachePath).display(container, url, null, callBack);
	}

	/**
	 * 显示view图片
	 *
	 * @param context
	 *            上下文
	 * @param container
	 *            容器
	 * @param url
	 *            地址
	 * @param diskCachePath
	 *            本地缓存路径
	 * @param displayConfig
	 *            显示配置
	 */
	public static <T extends View> void display(Context context, T container, String url, String diskCachePath, BitmapDisplayConfig displayConfig) {
		BitmapLoader.getInstance(context, diskCachePath).display(container, url, displayConfig, null);
	}

	/**
	 * 显示view图片
	 *
	 * @param context
	 *            上下文
	 * @param container
	 *            容器
	 * @param url
	 *            地址
	 * @param diskCachePath
	 *            本地缓存路径
	 * @param displayConfig
	 *            显示配置
	 * @param callBack
	 *            加载回调
	 */
	public static <T extends View> void display(Context context, T container, String url, String diskCachePath, BitmapDisplayConfig displayConfig, BitmapLoadCallBack<T> callBack) {
		BitmapLoader.getInstance(context, diskCachePath).display(container, url, displayConfig, callBack);
	}

	/**
	 * 清除缓存
	 *
	 * @param context 上下文
	 *
	 */
	public static void clearCache(Context context) {
		clearCache(context, null);
	}

	/**
	 * 清除缓存
	 *
	 * @param context 上下文
	 * @param diskCachePath 本地缓存路径
	 *
	 */
	public static void clearCache(Context context, String diskCachePath) {
		BitmapLoader.getInstance(context, diskCachePath).clearCache();
	}

	 /**
	 * 根据url地址获取Bitmap
	 *
	 * @param  url 地址
	 * @return bitmap对象
	 */
	public synchronized Bitmap getBitMap(final String url) {
		URL fileUrl = null;
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			fileUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
			}
			is = null;
		}
		return bitmap;
	}

	/**
	 * 根据本地路径获取Bitmap
	 *
	 * @param filePath 本地路径
	 * @return bitmap对象
	 */
	public synchronized static Bitmap getBitmap(String filePath) {
		Bitmap bitmap = null;
		if (!XCStringUtil.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				// 判断文件是否存在
				if (file.exists()) {
					bitmap = BitmapFactory.decodeFile(filePath);
				}
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 根据uri获取Bitmap
	 *
	 * @param context 上下文
	 * @param uri uri对象
	 * @deprecated 默认缩略图是原图大小的1/4
	 * @return bitmap对象
	 */
	public static Bitmap getBitmap(Context context, Uri uri) {
		return getBitmap(context, uri, 4);
	}

	/**
	 * 根据uri获取Bitmap
	 *
	 * @param context 上下文
	 * @param uri uri对象
	 * @param inSampleSize 缩略图缩小倍数
	 * @return bitmap对象
	 */
	public static Bitmap getBitmap(Context context, Uri uri, int inSampleSize) {
		Bitmap bitmap = null;
		if (context != null && uri != null) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				options.inSampleSize = inSampleSize;
				options.inPreferredConfig = Config.RGB_565;
				options.inPurgeable = true;
				options.inInputShareable = true;
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 将View转化为Bitmap
	 *
	 * @param view view对象
	 * @return bitmap对象
	 */
	public static Bitmap getViewToBitmap(View view) {
		Bitmap bitmap = null;
		try {
			int width = view.getWidth();
			int height = view.getHeight();
			if (width != 0 && height != 0) {
				bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				view.draw(canvas);
			}
		} catch (Exception e) {
		}
		return bitmap;
	}

	/**
	 * 将Drawable转化为Bitmap
	 *
	 * @param drawable drawable对象
	 * @return bitmap对象
	 */
	public static Bitmap getDrawableToBitmap(Drawable drawable) {
		Bitmap bitmap = null;
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			//
			bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
			//
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, width, height);
			drawable.draw(canvas);// 重点
		}
		return bitmap;
	}

	/**
	 * 将Bitmap转化为Drawable
	 *
	 * @param bitmap bitmap对象
	 * @return drawable对象
	 */
	@SuppressWarnings("deprecation")
	public static Drawable getBitmapToDrawable(Bitmap bitmap) {
		BitmapDrawable bitmapDrawable = null;
		if (bitmap != null) {
			bitmapDrawable = new BitmapDrawable(bitmap);
		}
		return bitmapDrawable;
	}

	/**
	 * 将Bitmap转换为字节数组
	 *
	 * @param bitmap bitmap对象
	 * @return 字节数组
	 */
	public static byte[] getBitmapToBytes(Bitmap bitmap) {
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			return baos.toByteArray();
		}
		return null;
	}

	/**
	 * 将字节数组转换为Bitmap
	 *
	 * @param bytes 字节数组
	 * @return bitmap对象
	 */
	public static Bitmap getBytesToBitmap(byte[] bytes) {
		Bitmap bitmap = null;
		if (bytes != null && bytes.length > 0) {
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		return bitmap;
	}

	/**
	 * 缩放图片
	 *
	 * @param bitmap bitmap对象
	 * @param width 宽
	 * @param height 高
	 * @return bitmap对象
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		Bitmap createBitmap = null;
		if (bitmap != null && width > 0 && height > 0) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) width / w);
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleWidth, scaleHeight);
			createBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
			bitmap.recycle();
			bitmap = null;
		}
		return createBitmap;
	}

	/**
	 * 缩放图片
	 *
	 * @param bitmap bitmap对象
	 * @param multiple 缩放比例
	 * @return bitmap对象
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, float multiple) {
		Bitmap resizeBmp = null;
		if (bitmap != null && multiple > 0) {
			Matrix matrix = new Matrix();
			matrix.postScale(multiple, multiple); // 长和宽放大缩小的比例
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		return resizeBmp;
	}

	/**
	 * 旋转图片
	 *
	 * @param bitmap bitmap对象
	 * @param degrees 角度
	 * @return bitmap对象
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
		Bitmap resizeBmp = null;
		if (bitmap != null && degrees > 0) {
			Matrix matrix = new Matrix();
			matrix.setRotate(degrees);
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		return resizeBmp;
	}

	/**
	 * 根据图片路径读取图片旋转角度
	 *
	 * @param filePath 图片路径
	 * @return 旋转角度
	 */
	public static int readPictureDegree(String filePath) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(filePath);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (Exception e) {
		}
		return degree;
	}

	/**
	 * 保存图片到本地
	 *
	 * @param context 上下文
	 * @param bitmap bitmap对象
	 * @param pictureName 图片名
	 * @return 保存路径
	 */
	public static String saveBitmap(Context context, Bitmap bitmap, String pictureName) {
		String path = "";
		try {
			if (context != null && bitmap != null && !XCStringUtil.isEmpty(pictureName)) {
				path = XCFileUtil.getExternalCacheDir(context) + File.separator + pictureName;
				File f = new File(path);
				if (f.exists()) {
					f.delete();
				}
				FileOutputStream out = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
		}
		return path;
	}

	/**
	 * 从URI获取本地路径
	 *
	 * @param context 上下文
	 * @param uri uri对象
	 * @return 本地路径
	 */
	public static String getAbsoluteImagePath(Context context, Uri uri) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(uri, null, null, null, null);
		if (cursor != null) {// 相册
			try {
				cursor.moveToFirst();
				String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路
				return filePath;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		} else {// 拍照
			return uri.getPath();
		}
		return null;
	}

	/**
	 * Bitmap添加水印
	 *
	 * @param src 原图bitmap对象
	 * @param watermark 水印bitmap对象
	 * @return 完成bitmap对象
	 */
	public static Bitmap addWatermark(Bitmap src, Bitmap watermark) {
		if (src == null || watermark == null) {
			return src;
		}
		int sWid = src.getWidth();
		int sHei = src.getHeight();
		int wWid = watermark.getWidth();
		int wHei = watermark.getHeight();
		if (sWid == 0 || sHei == 0) {
			return null;
		}
		if (sWid < wWid || sHei < wHei) {
			return src;
		}
		Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Config.ARGB_8888);// Config可修改,改变内存占用
		try {
			Canvas cv = new Canvas(bitmap);
			cv.drawBitmap(src, 0, 0, null);
			cv.drawBitmap(watermark, sWid - wWid - 5, sHei - wHei - 5, null);
			cv.save(Canvas.ALL_SAVE_FLAG);
			cv.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		} finally {
			src.recycle();
			src = null;
			watermark.recycle();
			watermark = null;
		}
		return bitmap;
	}

	/**
	 * 获取模糊Bitmap
	 * @param sentBitmap bitmap对象
	 * @param radius 模糊半径
	 * @return 完成bitmap对象
	 */
	public static Bitmap getBlurBitmap(Bitmap sentBitmap, int radius) {
		if (sentBitmap != null && radius > 1) {
			Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
			//
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			//
			int[] pix = new int[w * h];
			bitmap.getPixels(pix, 0, w, 0, 0, w, h);
			//
			int wm = w - 1;
			int hm = h - 1;
			int wh = w * h;
			int div = radius + radius + 1;
			//
			int r[] = new int[wh];
			int g[] = new int[wh];
			int b[] = new int[wh];
			int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
			int vmin[] = new int[Math.max(w, h)];
			//
			int divsum = (div + 1) >> 1;
			divsum *= divsum;
			int dv[] = new int[256 * divsum];
			for (i = 0; i < 256 * divsum; i++) {
				dv[i] = (i / divsum);
			}
			//
			yw = yi = 0;
			//
			int[][] stack = new int[div][3];
			int stackpointer;
			int stackstart;
			int[] sir;
			int rbs;
			int r1 = radius + 1;
			int routsum, goutsum, boutsum;
			int rinsum, ginsum, binsum;
			//
			for (y = 0; y < h; y++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				for (i = -radius; i <= radius; i++) {
					p = pix[yi + Math.min(wm, Math.max(i, 0))];
					sir = stack[i + radius];
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);
					rbs = r1 - Math.abs(i);
					rsum += sir[0] * rbs;
					gsum += sir[1] * rbs;
					bsum += sir[2] * rbs;
					if (i > 0) {
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}
				}
				stackpointer = radius;
				//
				for (x = 0; x < w; x++) {
					r[yi] = dv[rsum];
					g[yi] = dv[gsum];
					b[yi] = dv[bsum];
					//
					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;
					//
					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];
					//
					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];
					//
					if (y == 0) {
						vmin[x] = Math.min(x + radius + 1, wm);
					}
					p = pix[yw + vmin[x]];
					//
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);
					//
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					//
					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;
					//
					stackpointer = (stackpointer + 1) % div;
					sir = stack[(stackpointer) % div];
					//
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					//
					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];
					//
					yi++;
				}
				yw += w;
			}
			for (x = 0; x < w; x++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				yp = -radius * w;
				for (i = -radius; i <= radius; i++) {
					yi = Math.max(0, yp) + x;
					//
					sir = stack[i + radius];
					//
					sir[0] = r[yi];
					sir[1] = g[yi];
					sir[2] = b[yi];
					//
					rbs = r1 - Math.abs(i);
					//
					rsum += r[yi] * rbs;
					gsum += g[yi] * rbs;
					bsum += b[yi] * rbs;
					//
					if (i > 0) {
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}
					//
					if (i < hm) {
						yp += w;
					}
				}
				yi = x;
				stackpointer = radius;
				for (y = 0; y < h; y++) {
					// Preserve alpha channel: ( 0xff000000 & pix[yi] )
					pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
					//
					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;
					//
					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];
					//
					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];
					//
					if (x == 0) {
						vmin[y] = Math.min(y + r1, hm) * w;
					}
					p = x + vmin[y];
					//
					sir[0] = r[p];
					sir[1] = g[p];
					sir[2] = b[p];
					//
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					//
					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;
					//
					stackpointer = (stackpointer + 1) % div;
					sir = stack[stackpointer];
					//
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					//
					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];
					//
					yi += w;
				}
			}
			//
			bitmap.setPixels(pix, 0, w, 0, 0, w, h);
			return bitmap;
		}
		return null;
	}
}
