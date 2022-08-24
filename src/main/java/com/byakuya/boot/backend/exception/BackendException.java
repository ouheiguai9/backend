package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;
import lombok.Getter;

/**
 * Created by 田伯光 at 2022/8/22 23:32
 */
@Getter
public class BackendException extends RuntimeException {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    private ErrorCode errorCode;


    public BackendException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public BackendException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.reason, cause);
        this.errorCode = errorCode;
    }
}
