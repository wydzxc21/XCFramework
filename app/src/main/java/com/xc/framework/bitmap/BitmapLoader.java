package com.xc.framework.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;

import com.xc.framework.bitmap.cache.FileNameGenerator;
import com.xc.framework.bitmap.callback.BitmapLoadCallBack;
import com.xc.framework.bitmap.callback.BitmapLoadFrom;
import com.xc.framework.bitmap.callback.DefaultBitmapLoadCallBack;
import com.xc.framework.bitmap.core.AsyncDrawable;
import com.xc.framework.bitmap.core.BitmapSize;
import com.xc.framework.bitmap.download.Downloader;
import com.xc.framework.bitmap.task.PriorityAsyncTask;
import com.xc.framework.bitmap.task.PriorityExecutor;
import com.xc.framework.bitmap.task.TaskHandler;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap
 * @description
 */
public class BitmapLoader implements TaskHandler {

	private boolean pauseTask = false;
	private boolean cancelAllTask = false;
	private final Object pauseTaskLock = new Object();

	private Context context;
	private BitmapGlobalConfig globalConfig;
	private BitmapDisplayConfig defaultDisplayConfig;
	public static BitmapLoader mBitmapLoader;

	// ////////////////// create////////////////////////
	public synchronized static BitmapLoader getInstance(Context context) {
		if (mBitmapLoader == null) {
			mBitmapLoader = new BitmapLoader(context);
		}
		return mBitmapLoader;
	}

	public synchronized static BitmapLoader getInstance(Context context, String diskCachePath) {
		if (mBitmapLoader == null) {
			mBitmapLoader = new BitmapLoader(context, diskCachePath);
		}
		return mBitmapLoader;
	}

	public BitmapLoader(Context context) {
		this(context, null);
	}

	public BitmapLoader(Context context, String diskCachePath) {
		if (context == null) {
			throw new IllegalArgumentException("context may not be null");
		}
		this.context = context.getApplicationContext();
		globalConfig = BitmapGlobalConfig.getInstance(this.context, diskCachePath);
		defaultDisplayConfig = new BitmapDisplayConfig();
	}

	public BitmapLoader(Context context, String diskCachePath, int memoryCacheSize) {
		this(context, diskCachePath);
		globalConfig.setMemoryCacheSize(memoryCacheSize);
	}

	public BitmapLoader(Context context, String diskCachePath, int memoryCacheSize, int diskCacheSize) {
		this(context, diskCachePath);
		globalConfig.setMemoryCacheSize(memoryCacheSize);
		globalConfig.setDiskCacheSize(diskCacheSize);
	}

	public BitmapLoader(Context context, String diskCachePath, float memoryCachePercent) {
		this(context, diskCachePath);
		globalConfig.setMemCacheSizePercent(memoryCachePercent);
	}

	public BitmapLoader(Context context, String diskCachePath, float memoryCachePercent, int diskCacheSize) {
		this(context, diskCachePath);
		globalConfig.setMemCacheSizePercent(memoryCachePercent);
		globalConfig.setDiskCacheSize(diskCacheSize);
	}

	// ////////////////////////////////////// config
	// ////////////////////////////////////////////////////////////////////

	public BitmapLoader configDefaultLoadingImage(Drawable drawable) {
		defaultDisplayConfig.setLoadingDrawable(drawable);
		return this;
	}

	public BitmapLoader configDefaultLoadingImage(int resId) {
		defaultDisplayConfig.setLoadingDrawable(context.getResources().getDrawable(resId));
		return this;
	}

	public BitmapLoader configDefaultLoadingImage(Bitmap bitmap) {
		defaultDisplayConfig.setLoadingDrawable(new BitmapDrawable(context.getResources(), bitmap));
		return this;
	}

	public BitmapLoader configDefaultLoadFailedImage(Drawable drawable) {
		defaultDisplayConfig.setLoadFailedDrawable(drawable);
		return this;
	}

	public BitmapLoader configDefaultLoadFailedImage(int resId) {
		defaultDisplayConfig.setLoadFailedDrawable(context.getResources().getDrawable(resId));
		return this;
	}

	public BitmapLoader configDefaultLoadFailedImage(Bitmap bitmap) {
		defaultDisplayConfig.setLoadFailedDrawable(new BitmapDrawable(context.getResources(), bitmap));
		return this;
	}

	public BitmapLoader configDefaultBitmapMaxSize(int maxWidth, int maxHeight) {
		defaultDisplayConfig.setBitmapMaxSize(new BitmapSize(maxWidth, maxHeight));
		return this;
	}

	public BitmapLoader configDefaultBitmapMaxSize(BitmapSize maxSize) {
		defaultDisplayConfig.setBitmapMaxSize(maxSize);
		return this;
	}

	public BitmapLoader configDefaultImageLoadAnimation(Animation animation) {
		defaultDisplayConfig.setAnimation(animation);
		return this;
	}

	public BitmapLoader configDefaultAutoRotation(boolean autoRotation) {
		defaultDisplayConfig.setAutoRotation(autoRotation);
		return this;
	}

	public BitmapLoader configDefaultShowOriginal(boolean showOriginal) {
		defaultDisplayConfig.setShowOriginal(showOriginal);
		return this;
	}

	public BitmapLoader configDefaultBitmapConfig(Bitmap.Config config) {
		defaultDisplayConfig.setBitmapConfig(config);
		return this;
	}

	public BitmapLoader configDefaultDisplayConfig(BitmapDisplayConfig displayConfig) {
		defaultDisplayConfig = displayConfig;
		return this;
	}

	public BitmapLoader configDownloader(Downloader downloader) {
		globalConfig.setDownloader(downloader);
		return this;
	}

	public BitmapLoader configDefaultCacheExpiry(long defaultExpiry) {
		globalConfig.setDefaultCacheExpiry(defaultExpiry);
		return this;
	}

	public BitmapLoader configDefaultConnectTimeout(int connectTimeout) {
		globalConfig.setDefaultConnectTimeout(connectTimeout);
		return this;
	}

	public BitmapLoader configDefaultReadTimeout(int readTimeout) {
		globalConfig.setDefaultReadTimeout(readTimeout);
		return this;
	}

	public BitmapLoader configThreadPoolSize(int threadPoolSize) {
		globalConfig.setThreadPoolSize(threadPoolSize);
		return this;
	}

	public BitmapLoader configMemoryCacheEnabled(boolean enabled) {
		globalConfig.setMemoryCacheEnabled(enabled);
		return this;
	}

	public BitmapLoader configDiskCacheEnabled(boolean enabled) {
		globalConfig.setDiskCacheEnabled(enabled);
		return this;
	}

	public BitmapLoader configDiskCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
		globalConfig.setFileNameGenerator(fileNameGenerator);
		return this;
	}

	public BitmapLoader configBitmapCacheListener(BitmapCacheListener listener) {
		globalConfig.setBitmapCacheListener(listener);
		return this;
	}

	// //////////////////////// display ////////////////////////////////////

	public <T extends View> void display(T container, String uri) {
		display(container, uri, null, null);
	}

	public <T extends View> void display(T container, String uri, BitmapLoadCallBack<T> callBack) {
		display(container, uri, null, callBack);
	}

	public <T extends View> void display(T container, String uri, BitmapDisplayConfig displayConfig) {
		display(container, uri, displayConfig, null);
	}

	public <T extends View> void display(T container, String uri, BitmapDisplayConfig displayConfig, BitmapLoadCallBack<T> callBack) {
		if (container == null) {
			return;
		}

		if (callBack == null) {
			callBack = new DefaultBitmapLoadCallBack<T>();
		}

		if (displayConfig == null || displayConfig == defaultDisplayConfig) {
			displayConfig = defaultDisplayConfig.cloneNew();
		}

		// Optimize Max Size
		BitmapSize size = displayConfig.getBitmapMaxSize();
		displayConfig.setBitmapMaxSize(BitmapOtherUtil.optimizeMaxSizeByView(container, size.getWidth(), size.getHeight()));

		container.clearAnimation();

		if (TextUtils.isEmpty(uri)) {
			callBack.onLoadFailed(container, uri, displayConfig.getLoadFailedDrawable());
			return;
		}

		// start loading
		callBack.onPreLoad(container, uri, displayConfig);

		// find bitmap from mem cache.
		Bitmap bitmap = globalConfig.getBitmapCache().getBitmapFromMemCache(uri, displayConfig);

		if (bitmap != null) {
			callBack.onLoadStarted(container, uri, displayConfig);
			callBack.onLoadCompleted(container, uri, bitmap, displayConfig, BitmapLoadFrom.MEMORY_CACHE);
		} else if (!bitmapLoadTaskExist(container, uri, callBack)) {

			final BitmapLoadTask<T> loadTask = new BitmapLoadTask<T>(container, uri, displayConfig, callBack);

			// get executor
			PriorityExecutor executor = globalConfig.getBitmapLoadExecutor();
			File diskCacheFile = this.getBitmapFileFromDiskCache(uri);
			boolean diskCacheExist = diskCacheFile != null && diskCacheFile.exists();
			if (diskCacheExist && executor.isBusy()) {
				executor = globalConfig.getDiskCacheExecutor();
			}
			// set loading image
			Drawable loadingDrawable = displayConfig.getLoadingDrawable();
			callBack.setDrawable(container, new AsyncDrawable<T>(loadingDrawable, loadTask));

			loadTask.setPriority(displayConfig.getPriority());
			loadTask.executeOnExecutor(executor);
		}
	}

	// ///////////////////////////////////////////// cache
	// /////////////////////////////////////////////////////////////////

	public void clearCache() {
		globalConfig.clearCache();
	}

	public void clearMemoryCache() {
		globalConfig.clearMemoryCache();
	}

	public void clearDiskCache() {
		globalConfig.clearDiskCache();
	}

	public void clearCache(String uri) {
		globalConfig.clearCache(uri);
	}

	public void clearMemoryCache(String uri) {
		globalConfig.clearMemoryCache(uri);
	}

	public void clearDiskCache(String uri) {
		globalConfig.clearDiskCache(uri);
	}

	public void flushCache() {
		globalConfig.flushCache();
	}

	public void closeCache() {
		globalConfig.closeCache();
	}

	public File getBitmapFileFromDiskCache(String uri) {
		return globalConfig.getBitmapCache().getBitmapFileFromDiskCache(uri);
	}

	public Bitmap getBitmapFromMemCache(String uri, BitmapDisplayConfig config) {
		if (config == null) {
			config = defaultDisplayConfig;
		}
		return globalConfig.getBitmapCache().getBitmapFromMemCache(uri, config);
	}

	// //////////////////////////////////////// tasks
	// //////////////////////////////////////////////////////////////////////

	@Override
	public boolean supportPause() {
		return true;
	}

	@Override
	public boolean supportResume() {
		return true;
	}

	@Override
	public boolean supportCancel() {
		return true;
	}

	@Override
	public void pause() {
		pauseTask = true;
		flushCache();
	}

	@Override
	public void resume() {
		pauseTask = false;
		synchronized (pauseTaskLock) {
			pauseTaskLock.notifyAll();
		}
	}

	@Override
	public void cancel() {
		pauseTask = true;
		cancelAllTask = true;
		synchronized (pauseTaskLock) {
			pauseTaskLock.notifyAll();
		}
	}

	@Override
	public boolean isPaused() {
		return pauseTask;
	}

	@Override
	public boolean isCancelled() {
		return cancelAllTask;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	private static <T extends View> BitmapLoadTask<T> getBitmapTaskFromContainer(T container, BitmapLoadCallBack<T> callBack) {
		if (container != null) {
			final Drawable drawable = callBack.getDrawable(container);
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable<T> asyncDrawable = (AsyncDrawable<T>) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	private static <T extends View> boolean bitmapLoadTaskExist(T container, String uri, BitmapLoadCallBack<T> callBack) {
		final BitmapLoadTask<T> oldLoadTask = getBitmapTaskFromContainer(container, callBack);

		if (oldLoadTask != null) {
			final String oldUrl = oldLoadTask.uri;
			if (TextUtils.isEmpty(oldUrl) || !oldUrl.equals(uri)) {
				oldLoadTask.cancel(true);
			} else {
				return true;
			}
		}
		return false;
	}

	public class BitmapLoadTask<T extends View> extends PriorityAsyncTask<Object, Object, Bitmap> {
		private final String uri;
		private final WeakReference<T> containerReference;
		private final BitmapLoadCallBack<T> callBack;
		private final BitmapDisplayConfig displayConfig;

		private BitmapLoadFrom from = BitmapLoadFrom.DISK_CACHE;

		public BitmapLoadTask(T container, String uri, BitmapDisplayConfig config, BitmapLoadCallBack<T> callBack) {
			if (container == null || uri == null || config == null || callBack == null) {
				throw new IllegalArgumentException("args may not be null");
			}

			this.containerReference = new WeakReference<T>(container);
			this.callBack = callBack;
			this.uri = uri;
			this.displayConfig = config;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {

			synchronized (pauseTaskLock) {
				while (pauseTask && !this.isCancelled()) {
					try {
						pauseTaskLock.wait();
						if (cancelAllTask) {
							return null;
						}
					} catch (Throwable e) {
					}
				}
			}

			Bitmap bitmap = null;

			// get cache from disk cache
			if (!this.isCancelled() && this.getTargetContainer() != null) {
				this.publishProgress(PROGRESS_LOAD_STARTED);
				bitmap = globalConfig.getBitmapCache().getBitmapFromDiskCache(uri, displayConfig);
			}

			// download image
			if (bitmap == null && !this.isCancelled() && this.getTargetContainer() != null) {
				bitmap = globalConfig.getBitmapCache().downloadBitmap(uri, displayConfig, this);
				from = BitmapLoadFrom.URI;
			}

			return bitmap;
		}

		public void updateProgress(long total, long current) {
			this.publishProgress(PROGRESS_LOADING, total, current);
		}

		private static final int PROGRESS_LOAD_STARTED = 0;
		private static final int PROGRESS_LOADING = 1;

		@Override
		protected void onProgressUpdate(Object... values) {
			if (values == null || values.length == 0)
				return;

			final T container = this.getTargetContainer();
			if (container == null)
				return;

			switch ((Integer) values[0]) {
			case PROGRESS_LOAD_STARTED:
				callBack.onLoadStarted(container, uri, displayConfig);
				break;
			case PROGRESS_LOADING:
				if (values.length != 3)
					return;
				callBack.onLoading(container, uri, displayConfig, (Long) values[1], (Long) values[2]);
				break;
			default:
				break;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			final T container = this.getTargetContainer();
			if (container != null) {
				if (bitmap != null) {
					callBack.onLoadCompleted(container, this.uri, bitmap, displayConfig, from);
				} else {
					callBack.onLoadFailed(container, this.uri, displayConfig.getLoadFailedDrawable());
				}
			}
		}

		@Override
		protected void onCancelled(Bitmap bitmap) {
			synchronized (pauseTaskLock) {
				pauseTaskLock.notifyAll();
			}
		}

		public T getTargetContainer() {
			final T container = containerReference.get();
			final BitmapLoadTask<T> bitmapWorkerTask = getBitmapTaskFromContainer(container, callBack);

			if (this == bitmapWorkerTask) {
				return container;
			}

			return null;
		}
	}
}
