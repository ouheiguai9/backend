package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorCode;
import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by ganzl at 2022/4/28 11:55
 */
@Component
public class RequestAuthenticationManager implements AuthenticationManager {
    private final List<RequestAuthenticationProvider> providers;

    public RequestAuthenticationManager(List<RequestAuthenticationProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof RequestAuthenticationToken) {
            RequestAuthenticationToken token = (RequestAuthenticationToken) authentication;
            String authKey = token.getRequest().getHeader(ConstantUtils.AUTH_TYPE_KEY);
            if (StringUtils.hasText(authKey)) {
                for (RequestAuthenticationProvider provider : providers) {
                    if (!authKey.equals(provider.authKey())) continue;
                    Account account = provider.authenticate(token);
                    if (account == null) continue;
                    if (account.isLocked()) {
                        throw new BackendException(ErrorCode.AUTHENTICATION_DISABLE);
                    }
                    if (account.getLoginErrorCount() >= 5) {
                        throw new BackendException(ErrorCode.AUTHENTICATION_ERROR_LIMIT);
                    }
                }
            }
        }
        throw new BackendException(ErrorCode.AUTHENTICATION_FAIL);
    }
}
