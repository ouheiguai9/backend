package com.byakuya.boot.backend.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ResAPI {
    String code();

    String desc();

    String module() default "API";

    boolean onlyAdmin() default false;
}
