package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ganzl on 2021/2/4.
 */
@Slf4j
@RestControllerAdvice
public class BackendControllerAdvice {
    @ModelAttribute
    public void addTokenAttributes(HttpServletRequest request, Model model) {
        // todo
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus
    public ExceptionResponse globalException(Exception e) {
        log.error(ErrorStatus.CODE_UNKNOWN.reason, e);
        return ExceptionResponse.build();
    }

    @ExceptionHandler(BackendException.class)
    @ResponseStatus
    public ExceptionResponse globalException(BackendException e) {
        log.error(ErrorStatus.CODE_UNKNOWN.reason, e);
        return ExceptionResponse.build().setErrorStatus(e.getErrorStatus());
    }
}
