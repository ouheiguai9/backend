package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.config.ResAPI;
import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.Optional;

/**
 * Created by ganzl on 2020/4/3.
 */
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class WebSecurityConfig {
    @Value(ConstantUtils.DEFAULT_ERROR_PATH)
    private String errorUrl;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RequestLoginConfigurer loginConfigurer) throws Exception {
        SecurityErrorHandler securityErrorHandler = new SecurityErrorHandler();
        http.authorizeHttpRequests(authorize -> authorize.antMatchers(errorUrl, ConstantUtils.REST_API_PREFIX + "/**").permitAll().anyRequest().authenticated())
                .apply(loginConfigurer)
                .and()
                .exceptionHandling().authenticationEntryPoint(securityErrorHandler).accessDeniedHandler(securityErrorHandler)
                .and()
                .logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .and()
                .anonymous().disable().csrf().disable().headers().frameOptions().sameOrigin();
        return http.build();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor preFilterAuthorizationMethodInterceptor() {
        return new AuthorizationManagerBeforeMethodInterceptor(new AnnotationMatchingPointcut(null, ResAPI.class, true), (supplier, mi) -> new AuthorizationDecision(Optional.of(supplier.get()).map(authentication -> {
            if (AccountAuthentication.isAdmin(authentication)) return true;
            return false;
//            ResAPI resAPI = mi.getMethod().getAnnotation(ResAPI.class);
//            System.out.println(user.getUserId() + ":" + resAPI.desc());
//            return user.isAdmin() || (!resAPI.onlyAdmin() && user.getAuthority().check(resAPI.module(), resAPI.code()));
        }).orElse(false)));
    }

    @Bean
    HttpSessionIdResolver headerHttpSessionIdResolver() {
        return new HeaderHttpSessionIdResolver(ConstantUtils.HEADER_X_AUTH_TOKEN);
    }
}
