package com.byakuya.boot.backend.component.unique;

/**
 * Created by 田伯光 at 2022/10/10 2:11
 */
public enum Type {
    USERNAME("error.validation.user.username.exists"),
    EMAIL("error.validation.user.email.exists"),
    PHONE("error.validation.user.phone.exists");
    public final String errorCode;

    Type(String errorCode) {
        this.errorCode = errorCode;
    }
}
