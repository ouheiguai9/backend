package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.util.StringUtils;

/**
 * Created by 田伯光 at 2022/8/27 17:24
 */
public class RequestLoginConfigurer extends AbstractHttpConfigurer<RequestLoginConfigurer, HttpSecurity> {
    private final RequestAuthenticationFilter filter;
    private String loginUrl = "/login";

    public RequestLoginConfigurer(RequestAuthenticationFilter filter) {
        this.filter = filter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        MediaTypeRequestMatcher media = new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON);
        AntPathRequestMatcher path = new AntPathRequestMatcher(ConstantUtils.OPEN_API_PREFIX + loginUrl, HttpMethod.POST.name());
        filter.setRequiresAuthenticationRequestMatcher(new AndRequestMatcher(path, media));
        http.addFilterAfter(filter, LogoutFilter.class);
    }

    public RequestLoginConfigurer loginUrl(String url) {
        if (StringUtils.hasText(url)) {
            this.loginUrl = StringUtils.trimWhitespace(url);
        }
        return this;
    }
}
