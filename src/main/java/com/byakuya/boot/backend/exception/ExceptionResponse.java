package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2022/8/27 10:01
 */
@Data
@Accessors(chain = true)
public class ExceptionResponse implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    private final LocalDateTime timestamp = LocalDateTime.now();
    @JsonIgnore
    @NonNull
    private ErrorStatus errorStatus = ErrorStatus.CODE_UNKNOWN;
    private String path;
    private Serializable detail;

    public static ExceptionResponse build() {
        return new ExceptionResponse();
    }

    @JsonProperty
    public int getStatus() {
        return errorStatus.getHttpStatus().value();
    }

    @JsonProperty
    public int getErrorCode() {
        return errorStatus.value;
    }

    @JsonProperty
    public String getMessage() {
        return errorStatus.reason;
    }

}
