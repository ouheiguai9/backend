package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;

/**
 * Created by 田伯光 at 2022/8/22 23:49
 */
public class UnknownException extends BackendException {

    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    public UnknownException(Throwable cause) {
        super(ErrorStatus.CODE_UNKNOWN, cause);
    }
}
