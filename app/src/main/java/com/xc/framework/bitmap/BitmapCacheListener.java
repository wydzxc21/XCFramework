package com.xc.framework.bitmap;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap
 * @description
 */
public interface BitmapCacheListener {
    void onInitMemoryCacheFinished();

    void onInitDiskFinished();

    void onClearCacheFinished();

    void onClearMemoryCacheFinished();

    void onClearDiskCacheFinished();

    void onClearCacheFinished(String uri);

    void onClearMemoryCacheFinished(String uri);

    void onClearDiskCacheFinished(String uri);

    void onFlushCacheFinished();

    void onCloseCacheFinished();
}
