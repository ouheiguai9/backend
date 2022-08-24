package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ganzl on 2020/6/11.
 */
@Component
public class CustomizedErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        String errorAttributeStr = webRequest.getHeader(ConstantUtils.HEADER_ERROR_ATTRIBUTE_KEY);
        ErrorAttributeOptions newOptions = options.including(Include.EXCEPTION, Include.MESSAGE);
        if (!StringUtils.hasText(errorAttributeStr) || !errorAttributeStr.contains(Include.STACK_TRACE.toString())) {
            newOptions = newOptions.excluding(Include.STACK_TRACE, Include.BINDING_ERRORS);
        }
        Map<String, Object> rtnVal = super.getErrorAttributes(webRequest, newOptions);
        Throwable throwable = getError(webRequest);
        if (throwable instanceof BackendException) {
            rtnVal.put("code", ((BackendException) throwable).getErrorCode().value);
        } else if (throwable instanceof BindException) {
            List<ObjectError> errors = ((BindException) throwable).getAllErrors();
            if (!errors.isEmpty()) {
                rtnVal.put("message", errors.stream().map(error -> {
                    String property = error.getObjectName();
                    if (error instanceof FieldError) {
                        property = ((FieldError) error).getField();
                    }
                    return String.format("%s: %s", property, error.getDefaultMessage());
                }).collect(Collectors.joining("\r\n")));
            }
            rtnVal.remove("errors");
        }
        return rtnVal;
    }
}
