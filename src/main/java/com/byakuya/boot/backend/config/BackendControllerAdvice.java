package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 田伯光 on 2021/2/4.
 */
@Slf4j
@RestControllerAdvice
public class BackendControllerAdvice {
    @ModelAttribute
    public void addTokenAttributes(HttpServletRequest request, Model model) {
        // todo
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error(ErrorStatus.INVALID_PARAMETER_TYPE.reason, e);
        return createResponse(createBody(request, e).setErrorStatus(ErrorStatus.INVALID_PARAMETER_TYPE));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentException(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error(ErrorStatus.INVALID_PARAMETER_FIELD.reason, e);
        return createResponse(createBody(request, e).setErrorStatus(ErrorStatus.INVALID_PARAMETER_FIELD));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, AccessDeniedException e) {
        return createResponse(createBody(request, e).setErrorStatus(ErrorStatus.AUTHENTICATION_FORBIDDEN));
    }

    @ExceptionHandler(BackendException.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, BackendException e) {
        log.error(e.getErrorStatus().reason, e);
        return createResponse(createBody(request, e).setErrorStatus(e.getErrorStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, Exception e) {
        log.error(ErrorStatus.CODE_UNKNOWN.reason, e);
        return createResponse(createBody(request, e));
    }

    private ExceptionResponse createBody(HttpServletRequest request, Throwable e) {
        return ExceptionResponse.build().setPath(request.getRequestURI()).setDetail(e.getMessage());
    }

    private ResponseEntity<ExceptionResponse> createResponse(ExceptionResponse body) {
        return new ResponseEntity<>(body, body.getErrorStatus().getHttpStatus());
    }
}
