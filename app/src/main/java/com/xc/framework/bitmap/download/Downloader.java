
package com.xc.framework.bitmap.download;

import java.io.OutputStream;

import android.content.Context;

import com.xc.framework.bitmap.BitmapLoader;
/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap.download
 * @description
 */
public abstract class Downloader {

    /**
     * Download bitmap to outputStream by uri.
     *
     * @param uri
     * @param outputStream
     * @return The expiry time stamp or -1 if failed to download.
     */
    public abstract long downloadToStream(String uri, OutputStream outputStream, final BitmapLoader.BitmapLoadTask<?> task);

    private Context context;
    private long defaultExpiry;
    private int defaultConnectTimeout;
    private int defaultReadTimeout;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDefaultExpiry(long expiry) {
        this.defaultExpiry = expiry;
    }

    public long getDefaultExpiry() {
        return this.defaultExpiry;
    }

    public int getDefaultConnectTimeout() {
        return defaultConnectTimeout;
    }

    public void setDefaultConnectTimeout(int defaultConnectTimeout) {
        this.defaultConnectTimeout = defaultConnectTimeout;
    }

    public int getDefaultReadTimeout() {
        return defaultReadTimeout;
    }

    public void setDefaultReadTimeout(int defaultReadTimeout) {
        this.defaultReadTimeout = defaultReadTimeout;
    }
}
