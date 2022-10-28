package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.utils.ConstantUtils;
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
    private final ExceptionResponseConverter exceptionResponseConverter;

    public ErrorControllerImpl(ExceptionResponseConverter exceptionResponseConverter) {
        this.exceptionResponseConverter = exceptionResponseConverter;
    }

    @RequestMapping(ConstantUtils.DEFAULT_ERROR_PATH)
    public ResponseEntity<ExceptionResponse> error(HttpServletRequest request) {
        ExceptionResponse body = exceptionResponseConverter.toExceptionResponse(getException(request));
        return new ResponseEntity<>(body, body.errorStatus.httpStatus);
    }

    private Exception getException(HttpServletRequest request) {
        return (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    }
}
