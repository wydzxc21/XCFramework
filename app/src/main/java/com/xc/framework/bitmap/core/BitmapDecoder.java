package com.xc.framework.bitmap.core;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;
/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap.core
 * @description
 */
public class BitmapDecoder {

	private static final Object lock = new Object();
	private static final String TAG = "BitmapDecoder";

	private BitmapDecoder() {
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, BitmapSize maxSize, Bitmap.Config config) {
		synchronized (lock) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPurgeable = true;
			options.inInputShareable = true;
			BitmapFactory.decodeResource(res, resId, options);
			options.inSampleSize = calculateInSampleSize(options, maxSize.getWidth(), maxSize.getHeight());
			options.inJustDecodeBounds = false;
			if (config != null) {
				options.inPreferredConfig = config;
			}
			try {
				return BitmapFactory.decodeResource(res, resId, options);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename, BitmapSize maxSize, Bitmap.Config config) {
		synchronized (lock) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPurgeable = true;
			options.inInputShareable = true;
			BitmapFactory.decodeFile(filename, options);
			options.inSampleSize = calculateInSampleSize(options, maxSize.getWidth(), maxSize.getHeight());
			options.inJustDecodeBounds = false;
			if (config != null) {
				options.inPreferredConfig = config;
			}
			try {
				return BitmapFactory.decodeFile(filename, options);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
	}

	public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, BitmapSize maxSize, Bitmap.Config config) {
		synchronized (lock) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPurgeable = true;
			options.inInputShareable = true;
			BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
			options.inSampleSize = calculateInSampleSize(options, maxSize.getWidth(), maxSize.getHeight());
			options.inJustDecodeBounds = false;
			if (config != null) {
				options.inPreferredConfig = config;
			}
			try {
				return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
	}

	public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, BitmapSize maxSize, Bitmap.Config config) {
		synchronized (lock) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPurgeable = true;
			options.inInputShareable = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, options);
			options.inSampleSize = calculateInSampleSize(options, maxSize.getWidth(), maxSize.getHeight());
			options.inJustDecodeBounds = false;
			if (config != null) {
				options.inPreferredConfig = config;
			}
			try {
				return BitmapFactory.decodeByteArray(data, 0, data.length, options);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
	}

	public static Bitmap decodeResource(Resources res, int resId) {
		synchronized (lock) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			options.inInputShareable = true;
			try {
				return BitmapFactory.decodeResource(res, resId, options);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
	}

	public static Bitmap decodeFile(String filename) {
		synchronized (lock) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			options.inInputShareable = true;
			try {
				return BitmapFactory.decodeFile(filename, options);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
	}

	public static Bitmap decodeFileDescriptor(FileDescriptor fileDescriptor) {
		synchronized (lock) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			options.inInputShareable = true;
			try {
				return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
	}

	public static Bitmap decodeByteArray(byte[] data) {
		synchronized (lock) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			options.inInputShareable = true;
			try {
				return BitmapFactory.decodeByteArray(data, 0, data.length, options);
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (width > maxWidth || height > maxHeight) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) maxHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) maxWidth);
			}

			final float totalPixels = width * height;

			final float maxTotalPixels = maxWidth * maxHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > maxTotalPixels) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}
}
