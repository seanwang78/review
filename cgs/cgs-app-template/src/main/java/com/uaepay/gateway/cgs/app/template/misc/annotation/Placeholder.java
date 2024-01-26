package com.uaepay.gateway.cgs.app.template.misc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 国家化标识
 * @author 刘智斌
 * @version 0.1
 * @time 2020/2/27
 * @since 0.1
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface Placeholder {
}
