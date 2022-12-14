package com.byakuya.boot.backend.config;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * Created by 田伯光 at 2022/5/19 16:30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@RequestMapping
public @interface ApiModule {
    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};

    boolean secure() default true;
}
