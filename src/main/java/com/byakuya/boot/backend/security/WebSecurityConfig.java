package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/**
 * Created by ganzl on 2020/4/3.
 */
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RequestAuthenticationFilter requestAuthenticationFilter) throws Exception {
        String compositeLoginUrl = "/composite/login";
        MediaTypeRequestMatcher entryPointMatcher = new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON);
        entryPointMatcher.setUseEquals(true);
        SecurityErrorHandler securityErrorHandler = new SecurityErrorHandler();
        http.authorizeHttpRequests(authorize -> {
            try {
                authorize.antMatchers("/error", ConstantUtils.REST_API_PREFIX + "/**").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .anonymous().disable()
                        .logout().invalidateHttpSession(true)
                        .and()
                        .addFilterAfter(requestAuthenticationFilter, LogoutFilter.class)
                        .exceptionHandling().authenticationEntryPoint(securityErrorHandler).accessDeniedHandler(securityErrorHandler)
                        .and()
                        .csrf().disable()
                        .headers().frameOptions().sameOrigin();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
        requestAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AndRequestMatcher(new AntPathRequestMatcher(compositeLoginUrl, HttpMethod.POST.name()), new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)));
        requestAuthenticationFilter.setSessionAuthenticationStrategy(http.getSharedObject(SessionAuthenticationStrategy.class));
        return defaultSecurityFilterChain;
    }

//    @Bean
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    Advisor preFilterAuthorizationMethodInterceptor() {
//        return new AuthorizationManagerBeforeMethodInterceptor(new AnnotationMatchingPointcut(null, ResAPI.class, true), (supplier, mi) -> new AuthorizationDecision(Optional.of(supplier.get()).map(Authentication::getPrincipal).filter(x -> x instanceof AuthenticationUser).map(AuthenticationUser.class::cast).map(user -> {
//            ResAPI resAPI = mi.getMethod().getAnnotation(ResAPI.class);
//            System.out.println(user.getUserId() + ":" + resAPI.desc());
//            return user.isAdmin() || (!resAPI.onlyAdmin() && user.getAuthority().check(resAPI.module(), resAPI.code()));
//        }).orElse(false)));
//    }

    @Bean
    HttpSessionIdResolver headerHttpSessionIdResolver() {
        return new HeaderHttpSessionIdResolver(ConstantUtils.HEADER_X_AUTH_TOKEN);
    }
}
