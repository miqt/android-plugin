package com.miqt.pluginlib.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果希望被基层，请使用 HookMethodInherited
 * 作用于方法和class
 * 优先级：IgnoreMethodHook > HookMethod = HookMethodInherited
 * @author miqt
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface HookMethod {
}
