package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.parameter.ParameterService;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.utils.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 田伯光 at 2022/8/23 20:28
 */
@Slf4j
@Component
public class AdminAuthenticationProvider implements RequestAuthenticationProvider {
    private final static AtomicLong counter = new AtomicLong(0);

    private final ParameterService parameterService;

    public AdminAuthenticationProvider(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Override
    public AccountAuthentication authenticate(RequestAuthenticationToken token) throws AuthenticationException {
        long last = counter.get();
        if ((last >= ConstantUtils.LOGIN_ERROR_LIMIT_COUNT && last < 10000000) || LocalDateTime.now().minusMinutes(ConstantUtils.LOGIN_ERROR_WAIT_MINUTES).isBefore(LocalDateTime.ofInstant(Instant.ofEpochMilli(last), ZoneId.systemDefault()))) {
            throw new SecurityAuthenticationException(ErrorStatus.AUTHENTICATION_ERROR_LIMIT);
        }
        String randomKey = token.getRequest().getHeader(ConstantUtils.HEADER_X_AUTH_TOKEN);
        if (StringUtils.hasText(randomKey) && randomKey.equals(parameterService.getAdminRandomKey())) {
            counter.set(0);
            return AccountAuthentication.Admin.instance;
        }
        last = counter.incrementAndGet();
        if (last >= ConstantUtils.LOGIN_ERROR_LIMIT_COUNT) {
            counter.set(System.currentTimeMillis());
        }
        throw new SecurityAuthenticationException(ErrorStatus.AUTHENTICATION_FAIL);
    }

    @Override
    public String authKey() {
        return "admin";
    }


}
