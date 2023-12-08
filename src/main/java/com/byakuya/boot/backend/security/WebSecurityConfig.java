package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.authorization.AuthorizationService;
import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.utils.ConstantUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static com.byakuya.boot.backend.utils.ConstantUtils.HEADER_X_AUTH_TOKEN;

/**
 * Created by 田伯光 at 2022/10/12 23:56
 */
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class WebSecurityConfig {
    @Value(ConstantUtils.DEFAULT_ERROR_PATH)
    private String errorUrl;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RequestAuthenticationManager requestAuthenticationManager, AuthorizationManager<RequestAuthorizationContext> access) throws Exception {
        RequestAuthenticationFilter requestAuthenticationFilter = new RequestAuthenticationFilter(requestAuthenticationManager);
        RequestLoginConfigurer requestLoginConfigurer = new RequestLoginConfigurer(requestAuthenticationFilter);
        SecurityErrorHandler securityErrorHandler = new SecurityErrorHandler();
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().access(access))
                .apply(requestLoginConfigurer)
                .and()
                .exceptionHandling().authenticationEntryPoint(securityErrorHandler).accessDeniedHandler(securityErrorHandler)
                .and()
                .logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .and()
                .cors().and().anonymous().disable().csrf().disable().headers().frameOptions().sameOrigin();
        return http.build();
    }

    @Bean
    AuthorizationManager<RequestAuthorizationContext> requestMatcherAuthorizationManager(@Autowired(required = false) TenantPrefixMatcher tenantPrefixMatcher) {
        RequestMatcher permitAll = new OrRequestMatcher(new AntPathRequestMatcher(errorUrl), new AntPathRequestMatcher(ConstantUtils.OPEN_API_PREFIX + "/**"));
        return (supplier, context) -> {
            HttpServletRequest request = context.getRequest();
            boolean access = permitAll.matches(request) || Optional.ofNullable(supplier.get()).filter(x -> x.isAuthenticated() && x instanceof AccountAuthentication).map(authentication -> {
                AccountAuthentication accountAuthentication = (AccountAuthentication) authentication;
                if (tenantPrefixMatcher != null) {
                    Long tenantId = tenantPrefixMatcher.matches(request);
                    if (tenantId != null) {
                        return tenantId == accountAuthentication.getTenantId();
                    }
                }
                return true;
            }).orElse(false);
            return new AuthorizationDecision(access);
        };
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor aclMethodAuthorize() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(AclApiModule.class, AclApiMethod.class);
        return new AuthorizationManagerBeforeMethodInterceptor(pointcut, (supplier, mi) -> {
            boolean access = Optional.of(supplier.get()).filter(x -> x.isAuthenticated() && x instanceof AccountAuthentication).map(authentication -> {
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
            if (!access) {
                throw AuthException.forbidden(null);
            }
            return new AuthorizationDecision(true);
        });
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setExposedHeaders(Collections.singletonList(HEADER_X_AUTH_TOKEN));
        configuration.setMaxAge(Duration.ofHours(1));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    HttpSessionIdResolver headerHttpSessionIdResolver() {
        return new HeaderHttpSessionIdResolver(HEADER_X_AUTH_TOKEN);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();// 使用 BCrypt 加密
    }
}
