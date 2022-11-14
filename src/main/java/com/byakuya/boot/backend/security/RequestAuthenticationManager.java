package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.component.account.AccountService;
import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by 田伯光 at 2022/10/12 23:56
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
                        Account account = accountService.query(auth.getAccountId()).orElseThrow(() -> new UsernameNotFoundException(String.valueOf(auth.getAccountId())));
                        if (account.isLocked()) {
                            throw new LockedException("Account Locked");
                        }
                        if (account.getLoginErrorCount() >= ConstantUtils.LOGIN_ERROR_LIMIT_COUNT) {
                            throw new FailLimitException();
                        }
                        if (account.isAdmin()) {
                            auth.setTenantAdmin(account.isAdmin());
                        } else {
                            auth.setApis(accountService.getAccountApiAuth(auth.getAccountId()));
                        }
                    }
                    return auth;
                }
            }
        }
        throw new AuthenticationServiceException("Login fail");
    }
}
