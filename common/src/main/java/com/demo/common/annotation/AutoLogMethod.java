package com.demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author feng.yang
 * @date 2019-01-24
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AutoLogMethod {}
