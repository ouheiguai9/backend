package com.byakuya.boot.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by 田伯光 at 2022/8/22 23:06
 */
public enum ErrorStatus {
    CODE_UNKNOWN("error.unknown", HttpStatus.INTERNAL_SERVER_ERROR),
    CODE_NOT_FOUND("error.not.found", HttpStatus.NOT_FOUND),
    CODE_ARGUMENT("error.argument", HttpStatus.PRECONDITION_FAILED),
    CODE_SMS("error.sms", HttpStatus.INTERNAL_SERVER_ERROR),
    DB_RECORD_NOT_FOUND("error.db.record.not.found", HttpStatus.INTERNAL_SERVER_ERROR),
    DB_INTEGRITY_VIOLATION("error.db.integrity.violation", HttpStatus.INTERNAL_SERVER_ERROR),
    AUTH_INVALID_TOKEN("error.auth.invalid.token", HttpStatus.UNAUTHORIZED),
    AUTH_LOGIN_FAIL("error.auth.login.fail", HttpStatus.UNAUTHORIZED),
    AUTH_LOGIN_ACCOUNT_NOT_FOUND("error.auth.login.account.not.found", HttpStatus.UNAUTHORIZED),
    AUTH_LOGIN_INVALID_PASSWORD("error.auth.login.invalid.password", HttpStatus.UNAUTHORIZED),
    AUTH_LOGIN_ACCOUNT_DISABLE("error.auth.login.account.disable", HttpStatus.UNAUTHORIZED),
    AUTH_LOGIN_FAIL_LIMIT("error.auth.login.fail.limit", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_FORBIDDEN("error.auth.access.forbidden", HttpStatus.FORBIDDEN),
    VALIDATION_FAILED("error.validation.failed", HttpStatus.PRECONDITION_FAILED);

    public final String code;
    public final HttpStatus httpStatus;

    ErrorStatus(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
