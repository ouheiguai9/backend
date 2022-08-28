package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.AccountService;
import com.byakuya.boot.backend.exception.ErrorStatus;
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
    private final AccountService accountService;
    private final List<RequestAuthenticationProvider> providers;

    public RequestAuthenticationManager(AccountService accountService, List<RequestAuthenticationProvider> providers) {
        this.accountService = accountService;
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
                    AccountAuthentication auth = provider.authenticate(token);
                    if (auth == null) continue;
                    if (!AccountAuthentication.isAdmin(auth)) {
                        accountService.query(auth.getAccountId()).ifPresent(account -> {
                            if (account.isLocked()) {
                                throw new SecurityAuthenticationException(ErrorStatus.AUTHENTICATION_DISABLE);
                            }
                            if (account.getLoginErrorCount() >= 5) {
                                throw new SecurityAuthenticationException(ErrorStatus.AUTHENTICATION_ERROR_LIMIT);
                            }
                        });
                    }
                    return auth;
                }
            }
        }
        throw new SecurityAuthenticationException(ErrorStatus.AUTHENTICATION_FAIL);
    }
}
