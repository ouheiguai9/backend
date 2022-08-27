package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.exception.ErrorStatusGetter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 田伯光 at 2022/8/27 14:00
 */
public class SecurityErrorHandler implements AccessDeniedHandler, AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorStatus errorStatus = ErrorStatus.AUTHENTICATION_FAIL;
        if (authException instanceof ErrorStatusGetter) {
            errorStatus = ((ErrorStatusGetter) authException).getErrorStatus();
            request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, authException);
        } else {
            request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, new SecurityAuthenticationException(errorStatus, authException));
        }
        response.sendError(errorStatus.getHttpStatus().value(), errorStatus.reason);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorStatus errorStatus = ErrorStatus.AUTHENTICATION_FORBIDDEN;
        request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, new BackendException(errorStatus));
        response.sendError(errorStatus.getHttpStatus().value(), errorStatus.reason);
    }
}
