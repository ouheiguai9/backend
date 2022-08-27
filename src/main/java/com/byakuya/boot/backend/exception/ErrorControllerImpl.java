package com.byakuya.boot.backend.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by 田伯光 at 2022/8/27 12:21
 */
@Controller
public class ErrorControllerImpl implements ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<ExceptionResponse> error(HttpServletRequest request) {
        Exception exception = getException(request);
        ErrorStatus errorStatus;
        if (exception instanceof ErrorStatusGetter) {
            errorStatus = ((ErrorStatusGetter) exception).getErrorStatus();
        } else {
            errorStatus = ErrorStatus.CODE_UNKNOWN;
        }
        ExceptionResponse body = ExceptionResponse.build().setErrorStatus(errorStatus).setPath(getPath(request));
        return new ResponseEntity<>(body, errorStatus.getHttpStatus());
    }

    private Exception getException(HttpServletRequest request) {
        return (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    }

    private String getPath(HttpServletRequest request) {
        return (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
    }
}
