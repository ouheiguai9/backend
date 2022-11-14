package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.authorization.AuthorizationService;
import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.Optional;

/**
 * Created by 田伯光 at 2022/10/12 23:56
 */
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class WebSecurityConfig {
    @Value(ConstantUtils.DEFAULT_ERROR_PATH)
    private String errorUrl;
    @Value("${authentication-url:/authorizations/me}")
    private String authenticationUrl;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RequestAuthenticationManager requestAuthenticationManager) throws Exception {
        RequestAuthenticationFilter requestAuthenticationFilter = new RequestAuthenticationFilter(authenticationUrl, requestAuthenticationManager);
        SecurityErrorHandler securityErrorHandler = new SecurityErrorHandler();
        RequestLoginConfigurer loginConfigurer = new RequestLoginConfigurer(requestAuthenticationFilter);
        http.authorizeHttpRequests(authorize -> authorize.antMatchers(errorUrl, ConstantUtils.OPEN_API_PREFIX + "/**").permitAll().antMatchers(HttpMethod.OPTIONS).permitAll().anyRequest().authenticated())
                .apply(loginConfigurer)
                .and()
                .exceptionHandling().authenticationEntryPoint(securityErrorHandler).accessDeniedHandler(securityErrorHandler)
                .and()
                .logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .and()
                .cors().and().anonymous().disable().csrf().disable().headers().frameOptions().sameOrigin();
        return http.build();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor preFilterAuthorizationMethodInterceptor() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(AclApiModule.class, AclApiMethod.class);
        return new AuthorizationManagerBeforeMethodInterceptor(pointcut, (supplier, mi) -> {
            boolean bl = Optional.of(supplier.get()).filter(x -> x.isAuthenticated() && x instanceof AccountAuthentication).map(authentication -> {
                //超级管理员拥有所有操作权限
                if (AccountAuthentication.isAdmin(authentication)) return true;
                AclApiMethod apiMethod = mi.getMethod().getAnnotation(AclApiMethod.class);
                if (apiMethod.onlyAdmin()) return false;
                //租户管理员拥有所有非超管特定权限
                AccountAuthentication accountAuthentication = (AccountAuthentication) authentication;
                if (accountAuthentication.isTenantAdmin()) return true;
                AclApiModule apiModule = mi.getMethod().getDeclaringClass().getAnnotation(AclApiModule.class);
                return accountAuthentication.hasApiAuth(AuthorizationService.createAuthKey(apiModule.value(), apiMethod.value()));
            }).orElse(false);
            return new AuthorizationDecision(bl);
        });
    }

    @Bean
    HttpSessionIdResolver headerHttpSessionIdResolver() {
        return new HeaderHttpSessionIdResolver(ConstantUtils.HEADER_X_AUTH_TOKEN);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();// 使用 BCrypt 加密
    }
}
