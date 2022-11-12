package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

/**
 * Created by 田伯光 at 2022/10/22 23:48
 */
public class ValidationFailedException extends BackendException {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    private final BindException bindException;
    private final String errorCode;

    private ValidationFailedException(BindException bindException, String errorCode) {
        super(ErrorStatus.VALIDATION_FAILED, bindException);
        this.bindException = bindException;
        this.errorCode = errorCode;
    }

    public static BackendException buildWithCode(String errorCode) {
        Assert.hasText(errorCode, "");
        return new ValidationFailedException(null, errorCode);
    }

    public static BackendException buildWithBindException(BindException bindException) {
        Assert.notNull(bindException, "");
        return new ValidationFailedException(bindException, null);
    }

    @Override
    public String getMessage() {
        if (StringUtils.hasText(errorCode)) {
            return errorCode;
        }
        if (bindException != null && bindException.hasErrors()) {
            return bindException.getAllErrors().stream().findFirst().map(x -> StringUtils.hasText(x.getDefaultMessage()) ? x.getDefaultMessage() : x.getCode()).orElse(super.getMessage());
        }
        return super.getMessage();
    }
}
