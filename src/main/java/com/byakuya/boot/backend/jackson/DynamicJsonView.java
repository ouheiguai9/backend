package com.byakuya.boot.backend.jackson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ganzl on 2020/7/17.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicJsonView {
    String[] exclude() default {};

    String[] include() default {};

    Class<?> type();
}
