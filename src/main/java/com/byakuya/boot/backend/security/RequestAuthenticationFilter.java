package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 田伯光 at 2022/4/24 17:22
 */
public class RequestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public RequestAuthenticationFilter(String authenticationUrl, AuthenticationManager authenticationManager) {
        super(ConstantUtils.OPEN_API_PREFIX + "/login", authenticationManager);
        AuthenticationHandler authenticationHandler = new AuthenticationHandler(authenticationUrl);
        this.setAuthenticationSuccessHandler(authenticationHandler);
        this.setAuthenticationFailureHandler(authenticationHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        return getAuthenticationManager().authenticate(new RequestAuthenticationToken(request));
    }

    private static class AuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
        private final String authenticationUrl;

        private AuthenticationHandler(String authenticationUrl) {
            this.authenticationUrl = authenticationUrl;
        }

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            if (exception instanceof FailLimitException) {
                throw AuthException.loginFailLimit();
            } else if (exception instanceof LockedException) {
                throw AuthException.loginAccountDisable();
            } else {
                throw AuthException.loginFail(exception.getCause());
            }
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            request.getRequestDispatcher(authenticationUrl).forward(new SecurityContextHolderAwareRequestWrapper(request, null), response);
        }
    }
}
