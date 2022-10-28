package com.byakuya.boot.backend.exception;

import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Created by 田伯光 at 2022/10/21 11:23
 */
@Component
public class ExceptionResponseConverter implements MessageSourceAware {

    private final boolean includeTrace;
    private MessageSourceAccessor messageSourceAccessor;

    public ExceptionResponseConverter(Environment environment) {
        includeTrace = !Arrays.asList(environment.getActiveProfiles()).contains(ConstantUtils.ACTIVE_PRO_KEY);
    }

    public ExceptionResponse toExceptionResponse(Throwable error) {
        if (error == null) {
            error = new BackendException(ErrorStatus.CODE_UNKNOWN);
        }
        ErrorStatus errorStatus = ErrorStatus.CODE_UNKNOWN;
        String errorMsg = null;
        if (error instanceof MessageSourceResolvable) {
            errorMsg = messageSourceAccessor.getMessage((MessageSourceResolvable) error);
        }
        if (error instanceof BackendException) {
            errorStatus = ((BackendException) error).getErrorStatus();
            if (!StringUtils.hasText(errorMsg)) {
                errorMsg = error.getMessage();
                if (errorMsg.startsWith(ConstantUtils.ERROR_MESSAGE_CODE_PREFIX)) {
                    errorMsg = messageSourceAccessor.getMessage(errorMsg);
                }
            }
        }
        if (!StringUtils.hasText(errorMsg)) {
            errorMsg = StringUtils.hasText(error.getMessage()) ? error.getMessage() : errorStatus.code;
        }
        ExceptionResponse rtnVal = new ExceptionResponse(errorStatus, errorMsg);
        if (includeTrace) {
            rtnVal.setTrace(trace(error));
        }
        return rtnVal;
    }

    private String trace(Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        return stackTrace.toString();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }
}
