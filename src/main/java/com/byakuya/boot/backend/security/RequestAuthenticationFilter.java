package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.utils.ConstantUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * Created by 田伯光 at 2022/4/24 17:22
 */
public class RequestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public RequestAuthenticationFilter(RequestAuthenticationManager requestAuthenticationManager) {
        super(ConstantUtils.OPEN_API_PREFIX + "/login", requestAuthenticationManager);
        this.setAuthenticationSuccessHandler(requestAuthenticationManager.getSuccessHandler());
        this.setAuthenticationFailureHandler(requestAuthenticationManager.getFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        return getAuthenticationManager().authenticate(new RequestAuthenticationToken(request));
    }
}
