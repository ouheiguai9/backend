package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, AccessDeniedException e) {
        ExceptionResponse body = ExceptionResponse.build().setErrorStatus(ErrorStatus.AUTHENTICATION_FORBIDDEN).setPath(request.getRequestURI());
        return new ResponseEntity<>(body, body.getErrorStatus().getHttpStatus());
    }

    @ExceptionHandler(BackendException.class)
    @ResponseStatus
    public ExceptionResponse globalException(BackendException e) {
        log.error(ErrorStatus.CODE_UNKNOWN.reason, e);
        return ExceptionResponse.build().setErrorStatus(e.getErrorStatus());
    }
}
