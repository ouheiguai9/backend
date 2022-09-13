package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.utils.ConstantUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by ganzl at 2022/4/24 17:22
 */
@Component
public class RequestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public RequestAuthenticationFilter(RequestAuthenticationManager requestAuthenticationManager, ObjectMapper objectMapper) {
        super(ConstantUtils.OPEN_API_PREFIX + "/login", requestAuthenticationManager);
        AuthenticationHandler authenticationHandler = new AuthenticationHandler(objectMapper);
        this.setAuthenticationSuccessHandler(authenticationHandler);
        this.setAuthenticationFailureHandler(authenticationHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        return getAuthenticationManager().authenticate(new RequestAuthenticationToken(request));
    }

    private static class AuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
        private final ObjectMapper objectMapper;

        private AuthenticationHandler(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            throw exception;
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.setStatus(HttpStatus.OK.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(authentication));
        }
    }
}
