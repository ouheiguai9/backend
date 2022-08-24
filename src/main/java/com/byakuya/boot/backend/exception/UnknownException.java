package com.byakuya.boot.backend.exception;

/**
 * Created by 田伯光 at 2022/8/22 23:49
 */
public class UnknownException extends BackendException {

    public UnknownException(Throwable cause) {
        super(ErrorCode.CODE_UNKNOWN, cause);
    }
}
