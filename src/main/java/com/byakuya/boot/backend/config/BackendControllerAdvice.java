package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, DataIntegrityViolationException e) {
        ErrorStatus status = ErrorStatus.DB_CONSTRAINT_VIOLATION;
        Throwable cause = e.getRootCause();
        if (cause == null) {
            cause = e;
        }
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            switch (((SQLIntegrityConstraintViolationException) cause).getErrorCode()) {
                case 1062:
                    status = ErrorStatus.DB_RECORD_DUPLICATE;
                    break;
                case 1452:
                    status = ErrorStatus.DB_REL_RECORD_NOT_FOUND;
                    break;
                default:
            }
            return createResponse(createBody(request).setErrorStatus(status), cause);
        }
        return createResponse(createBody(request).setErrorStatus(status), e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, HttpMessageNotReadableException e) {
        return createResponse(createBody(request).setErrorStatus(ErrorStatus.INVALID_PARAMETER_TYPE), e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentException(HttpServletRequest request, MethodArgumentNotValidException e) {
        return createResponse(createBody(request).setErrorStatus(ErrorStatus.INVALID_PARAMETER_FIELD), e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, AccessDeniedException e) {
        return createResponse(createBody(request).setErrorStatus(ErrorStatus.AUTHENTICATION_FORBIDDEN), e);
    }

    @ExceptionHandler(BackendException.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, BackendException e) {
        return createResponse(createBody(request).setErrorStatus(e.getErrorStatus()), e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, Exception e) {
        return createResponse(createBody(request), e);
    }

    private ExceptionResponse createBody(HttpServletRequest request) {
        return ExceptionResponse.build().setPath(request.getRequestURI());
    }

    private ResponseEntity<ExceptionResponse> createResponse(ExceptionResponse body, Throwable e) {
        log.error(body.getErrorStatus().reason, e);
        return new ResponseEntity<>(body, body.setDetail(e.getMessage()).getErrorStatus().getHttpStatus());
    }
}
