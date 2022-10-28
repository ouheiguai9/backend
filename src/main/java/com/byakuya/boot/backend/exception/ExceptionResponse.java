package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.SystemVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/8/27 10:01
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @JsonIgnore
    public final ErrorStatus errorStatus;
    private final String message;
    private String trace;

    ExceptionResponse(ErrorStatus errorStatus, String message) {
        this.errorStatus = errorStatus;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getTrace() {
        return trace;
    }

    public ExceptionResponse setTrace(String trace) {
        this.trace = trace;
        return this;
    }

    @JsonProperty
    public int getStatus() {
        return errorStatus.httpStatus.value();
    }

    @JsonProperty
    public String getCode() {
        return errorStatus.code;
    }

}
