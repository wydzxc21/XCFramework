package com.xc.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZhangXuanChen
 * @date 2020/2/3
 * @package com.xc.framework.annotation
 * @description 注册注释（View注册，需配合XCViewUtil.initView()用）
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInit {
    /**
     * ViewId
     *
     */
    int value() default 0;

}
