package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Created by 田伯光 on 2021/2/4.
 */
@Slf4j
@RestControllerAdvice
public class BackendControllerAdvice {

    private final ExceptionResponseConverter exceptionResponseConverter;
    private final TenantValidator tenantValidator;

    public BackendControllerAdvice(ExceptionResponseConverter exceptionResponseConverter, TenantValidator tenantValidator) {
        this.exceptionResponseConverter = exceptionResponseConverter;
        this.tenantValidator = tenantValidator;
    }

    @ModelAttribute
    public void addTokenAttributes(HttpServletRequest request, Model model) {
        // todo
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(tenantValidator);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> dataViolationException(DataIntegrityViolationException e) {
        return createResponse(exceptionResponseConverter.toExceptionResponse(new IntegrityViolationException(e)));
    }

    private ResponseEntity<ExceptionResponse> createResponse(ExceptionResponse body) {
        return new ResponseEntity<>(body, body.errorStatus.httpStatus);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ExceptionResponse> transactionException(TransactionException e) {
        Throwable root = e.getRootCause();
        if (root instanceof ConstraintViolationException) {
            return createResponse(exceptionResponseConverter.toExceptionResponse(ValidationFailedException.buildWithCode(((ConstraintViolationException) root).getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElse(root.getMessage()))));
        }
        log.error("数据库事务异常", e);
        return createResponse(exceptionResponseConverter.toExceptionResponse(e));
    }

    //
////    @ExceptionHandler(HttpMessageNotReadableException.class)
////    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, HttpMessageNotReadableException e) {
////        return createResponse(createBody(request).setErrorStatus(ErrorStatus.INVALID_PARAMETER_TYPE), e);
////    }
//
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentException(BindException e) {
        return createResponse(exceptionResponseConverter.toExceptionResponse(ValidationFailedException.buildWithBindException(e)));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentException(ServletRequestBindingException e) {
        return createResponse(exceptionResponseConverter.toExceptionResponse(new BackendException(ErrorStatus.CODE_ARGUMENT, e)));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentException(TypeMismatchException e) {
        return createResponse(exceptionResponseConverter.toExceptionResponse(new BackendException(ErrorStatus.CODE_ARGUMENT, e)));
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ExceptionResponse> accessDeniedException(AccessDeniedException e) {
//        return createResponse(exceptionResponseConverter.toExceptionResponse(AuthException.forbidden(e)));
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> globalException(Exception e) {
        log.error("未知异常", e);
        return createResponse(exceptionResponseConverter.toExceptionResponse(e));
    }
}
