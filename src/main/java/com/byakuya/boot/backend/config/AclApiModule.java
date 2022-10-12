package com.byakuya.boot.backend.config;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Created by 田伯光 at 2022/5/19 16:30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiModule
public @interface AclApiModule {
    @AliasFor(annotation = ApiModule.class)
    String[] path() default {};

    String value();

    String desc();
}
