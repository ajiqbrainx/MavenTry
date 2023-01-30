package com.qbrainx.common.cache;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(CustomCacheConfiguration.class)
public @interface EnableCustomCache {
}
