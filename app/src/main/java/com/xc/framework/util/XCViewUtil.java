package com.xc.framework.util;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.View.MeasureSpec;

import com.xc.framework.annotation.ViewInit;

import java.lang.reflect.Field;

/**
 * @author ZhangXuanChen
 * @date 2016-11-17
 * @package com.xc.framework.utils
 * @description Viewx相关工具类
 */
public class XCViewUtil {
    /**
     * 测量宽高
     *
     * @param measureSpec 待测量宽高
     * @return 测量后宽高
     */
    public static int getMeasureSize(int measureSpec) {
        int result = 100;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //
        if (specMode == MeasureSpec.EXACTLY) { // fill_parent
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) { // wrap_content
            result = Math.min(result, specSize);
        }
        return result;
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/3
     * @description View注册（配合ViewInit注释用）
     */
    public static void initView(Activity activity) {
        try {
            Field[] fields = activity.getClass().getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    boolean isAnnotationPresent = field.isAnnotationPresent(ViewInit.class);
                    if (isAnnotationPresent) {
                        ViewInit mViewInit = field.getAnnotation(ViewInit.class);
                        if (mViewInit != null) {
                            field.setAccessible(true);
                            field.set(activity, activity.findViewById(mViewInit.value()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/2/3
     * @description View注册（配合ViewInit注释用）
     */
    public static void initView(Fragment fragment, View view) {
        try {
            Field[] fields = fragment.getClass().getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    ViewInit mViewInit = field.getAnnotation(ViewInit.class);
                    if (mViewInit != null) {
                        field.setAccessible(true);
                        field.set(fragment, view.findViewById(mViewInit.value()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
