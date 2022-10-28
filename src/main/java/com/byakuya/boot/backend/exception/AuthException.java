package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;

/**
 * Created by 田伯光 at 2022/10/21 10:19
 */
public final class AuthException extends BackendException {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    private AuthException(ErrorStatus errorStatus, Throwable cause) {
        super(errorStatus, cause);
    }

    public static AuthException invalidToken(Throwable cause) {
        return new AuthException(ErrorStatus.AUTH_INVALID_TOKEN, cause);
    }

    public static AuthException forbidden(Throwable cause) {
        return new AuthException(ErrorStatus.AUTH_ACCESS_FORBIDDEN, cause);
    }

    public static AuthException loginFail(Throwable cause) {
        return new AuthException(ErrorStatus.AUTH_LOGIN_FAIL, cause);
    }

    public static AuthException loginFailLimit() {
        return new AuthException(ErrorStatus.AUTH_LOGIN_FAIL_LIMIT, null);
    }

    public static AuthException loginAccountDisable() {
        return new AuthException(ErrorStatus.AUTH_LOGIN_ACCOUNT_DISABLE, null);
    }
}
