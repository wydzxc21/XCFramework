package com.xc.framework.bitmap.factory;

import android.graphics.Bitmap;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap.factory
 * @description
 */
public interface BitmapFactory {

    BitmapFactory cloneNew();

    Bitmap createBitmap(Bitmap rawBitmap);
}
