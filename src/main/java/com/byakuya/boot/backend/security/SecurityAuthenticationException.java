package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.exception.ErrorStatusGetter;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by 田伯光 at 2022/8/27 14:09
 */
public class SecurityAuthenticationException extends AuthenticationException implements ErrorStatusGetter {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Getter
    private final ErrorStatus errorStatus;

    public SecurityAuthenticationException(ErrorStatus errorStatus) {
        this(errorStatus, null);
    }

    public SecurityAuthenticationException(ErrorStatus errorStatus, Throwable cause) {
        super(errorStatus.reason, cause);
        this.errorStatus = errorStatus;
    }
}
