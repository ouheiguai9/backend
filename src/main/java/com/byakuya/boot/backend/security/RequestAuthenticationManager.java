package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.AccountService;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
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

    @Value("${authentication-url:/authorizations/me}")
    private String authenticationUrl;

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
                    auth.eraseCredentials();
                    return auth;
                }
            }
        }
        throw new AuthenticationServiceException("Login fail");
    }

    public AuthenticationSuccessHandler getSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication instanceof AccountAuthentication && !AccountAuthentication.isAdmin(authentication)) {
                accountService.loginSuccess(((AccountAuthentication) authentication).getAccount().getId());
            }
            request.getRequestDispatcher(authenticationUrl).forward(new SecurityContextHolderAwareRequestWrapper(request, null), response);
        };
    }

    public AuthenticationFailureHandler getFailureHandler() {
        return (request, response, exception) -> {
            if (exception instanceof FailLimitException) {
                throw AuthException.loginFailLimit();
            } else if (exception instanceof UsernameNotFoundException) {
                throw AuthException.loginAccountNotFound(exception);
            } else if (exception instanceof BadCredentialsException) {
                Throwable cause = exception.getCause();
                throw cause instanceof BackendException ? ((BackendException) cause) : AuthException.loginInvalidPassword(exception);
            } else if (exception instanceof LockedException) {
                throw AuthException.loginAccountDisable();
            } else {
                throw AuthException.loginFail(exception.getCause());
            }
        };
    }
}
