package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;
import org.springframework.util.StringUtils;

/**
 * Created by 田伯光 at 2022/9/13 11:29
 */
public class InvalidParameterException extends BackendException {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    InvalidParameterException() {
        super(ErrorStatus.INVALID_PARAMETER);
    }

    InvalidParameterException(String reason) {
        super(ErrorStatus.INVALID_PARAMETER, new IllegalArgumentException(reason));
    }

    public static InvalidParameterException build() {
        return build(null);
    }

    public static InvalidParameterException build(String reason) {
        return StringUtils.hasText(reason) ? new InvalidParameterException(reason) : new InvalidParameterException();
    }
}
