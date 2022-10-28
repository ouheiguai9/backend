package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.exception.ExceptionResponse;
import com.byakuya.boot.backend.exception.ExceptionResponseConverter;
import com.byakuya.boot.backend.exception.IntegrityViolationException;
import com.byakuya.boot.backend.exception.ValidationFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 田伯光 on 2021/2/4.
 */
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
    public ResponseEntity<ExceptionResponse> globalException(DataIntegrityViolationException e) {
        return createResponse(exceptionResponseConverter.toExceptionResponse(new IntegrityViolationException(e)));
    }

    //    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ExceptionResponse> globalException(HttpServletRequest request, AccessDeniedException e) {
//        return createResponse(createBody(request).setErrorStatus(ErrorStatus.AUTH_ACCESS_FORBIDDEN), e);
//    }
//
    private ResponseEntity<ExceptionResponse> createResponse(ExceptionResponse body) {
        return new ResponseEntity<>(body, body.errorStatus.httpStatus);
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
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> globalException(Exception e) {
        return createResponse(exceptionResponseConverter.toExceptionResponse(e));
    }
}
