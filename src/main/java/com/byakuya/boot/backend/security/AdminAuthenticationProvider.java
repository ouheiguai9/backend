package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.component.parameter.ParameterService;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorCode;
import com.byakuya.boot.backend.utils.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by 田伯光 at 2022/8/23 20:28
 */
@Slf4j
@Component
public class AdminAuthenticationProvider implements RequestAuthenticationProvider {

    private ParameterService parameterService;

    public AdminAuthenticationProvider(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Override
    public Account authenticate(RequestAuthenticationToken token) throws AuthenticationException {
        String randomKey = token.getRequest().getHeader(ConstantUtils.HEADER_X_AUTH_TOKEN);
        if (StringUtils.hasText(randomKey) && randomKey.equals(parameterService.getAdminRandomKey())) {

        }
        throw new BackendException(ErrorCode.AUTHENTICATION_FAIL);
    }

    @Override
    public String authKey() {
        return "admin";
    }
}
