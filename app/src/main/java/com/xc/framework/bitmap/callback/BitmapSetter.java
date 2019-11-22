package com.xc.framework.bitmap.callback;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap.callback
 * @description
 */
public interface BitmapSetter<T extends View> {
    void setBitmap(T container, Bitmap bitmap);

    void setDrawable(T container, Drawable drawable);

    Drawable getDrawable(T container);
}
