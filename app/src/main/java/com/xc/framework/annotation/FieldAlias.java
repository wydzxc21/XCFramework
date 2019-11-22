package com.xc.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date：2019/9/25
 * Author：ZhangXuanChen
 * Description：别名注释（字段映射别名）
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FieldAlias {
    /**
     * 映射的别名
     *
     * @return
     */
    String value() default "";
}
