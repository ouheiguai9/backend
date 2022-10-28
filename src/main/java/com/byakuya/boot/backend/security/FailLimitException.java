package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.SystemVersion;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by 田伯光 at 2022/10/21 10:36
 */
public class FailLimitException extends AuthenticationException {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    public FailLimitException() {
        super("Login fail limit");
    }
}
