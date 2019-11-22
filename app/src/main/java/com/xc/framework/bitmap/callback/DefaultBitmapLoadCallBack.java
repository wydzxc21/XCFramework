
package com.xc.framework.bitmap.callback;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;

import com.xc.framework.bitmap.BitmapDisplayConfig;

import java.lang.reflect.Method;
/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.xc.framework.bitmap.callback
 * @description
 */
public class DefaultBitmapLoadCallBack<T extends View> extends BitmapLoadCallBack<T> {

    @Override
    public void onLoadCompleted(T container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
        this.setBitmap(container, bitmap);
        Animation animation = config.getAnimation();
        if (animation != null) {
            animationDisplay(container, animation);
        }
    }

    @Override
    public void onLoadFailed(T container, String uri, Drawable drawable) {
        this.setDrawable(container, drawable);
    }

    private void animationDisplay(T container, Animation animation) {
        try {
            Method cloneMethod = Animation.class.getDeclaredMethod("clone");
            cloneMethod.setAccessible(true);
            container.startAnimation((Animation) cloneMethod.invoke(animation));
        } catch (Throwable e) {
            container.startAnimation(animation);
        }
    }
}
