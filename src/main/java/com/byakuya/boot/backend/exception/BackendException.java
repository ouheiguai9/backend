package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;
import lombok.Getter;

import java.util.Objects;

/**
 * Created by 田伯光 at 2022/8/22 23:32
 */
public class BackendException extends RuntimeException {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    @Getter
    private final ErrorStatus errorStatus;

    public BackendException(ErrorStatus errorStatus) {
        this(errorStatus, null);
    }

    public BackendException(ErrorStatus errorStatus, Throwable cause) {
        super(cause);
        Objects.requireNonNull(errorStatus, "Error code is required");
        this.errorStatus = errorStatus;
    }

    @Override
    public String getMessage() {
        return errorStatus.code;
    }
}
