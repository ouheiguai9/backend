package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.component.account.AccountService;
import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 田伯光 at 2022/12/14 22:00
 */
public abstract class AbstractAccountAuthenticationProvider implements RequestAuthenticationProvider {

    protected final AccountService accountService;

    protected AbstractAccountAuthenticationProvider(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public AccountAuthentication authenticate(RequestAuthenticationToken token) throws AuthenticationException {
        HttpServletRequest request = token.getRequest();
        AccountAuthentication auth = retrieveAuthentication(request);
        Account account = accountService.query(auth.getAccountId()).orElseThrow(() -> new UsernameNotFoundException(String.valueOf(auth.getAccountId())));
        if (account.isLocked()) {
            throw new LockedException("Account Locked");
        }
        if (account.getLoginErrorCount() >= ConstantUtils.LOGIN_ERROR_LIMIT_COUNT) {
            throw new FailLimitException();
        }
        try {
            additionalAuthenticationChecks(auth, request);
        } catch (BadCredentialsException e) {
            accountService.loginFail(account.getId());
            throw e;
        }

        if (account.isAdmin()) {
            auth.setTenantAdmin(account.isAdmin());
        } else {
            auth.setApis(accountService.getAccountApiAuth(auth.getAccountId()));
        }
        return auth;
    }

    protected abstract AccountAuthentication retrieveAuthentication(HttpServletRequest request) throws AuthenticationException;

    protected abstract void additionalAuthenticationChecks(AccountAuthentication auth, HttpServletRequest request) throws AuthenticationException;
}
