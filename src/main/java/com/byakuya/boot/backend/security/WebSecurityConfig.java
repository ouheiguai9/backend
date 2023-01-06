package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.authorization.AuthorizationService;
import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.config.TenantAuthorize;
import com.byakuya.boot.backend.utils.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

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
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, RequestAuthenticationManager requestAuthenticationManager) throws Exception {
        RequestAuthenticationFilter requestAuthenticationFilter = new RequestAuthenticationFilter(requestAuthenticationManager);
        RequestLoginConfigurer requestLoginConfigurer = new RequestLoginConfigurer(requestAuthenticationFilter);
        SecurityErrorHandler securityErrorHandler = new SecurityErrorHandler();
        http.authorizeHttpRequests(authorize -> authorize.antMatchers(errorUrl, ConstantUtils.OPEN_API_PREFIX + "/**").permitAll().antMatchers(HttpMethod.OPTIONS).permitAll().anyRequest().authenticated())
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor aclMethodAuthorize() {
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor tenantMethodAuthorize() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(TenantAuthorize.class, RequestMapping.class, true);
        return new AuthorizationManagerBeforeMethodInterceptor(pointcut, (supplier, mi) -> {
            boolean bl = Optional.of(supplier.get()).filter(x -> x.isAuthenticated() && x instanceof AccountAuthentication).map(authentication -> {
                Method method = mi.getMethod();
                TenantAuthorize tenantAuthorize;
                if (method.isAnnotationPresent(TenantAuthorize.class)) {
                    tenantAuthorize = method.getAnnotation(TenantAuthorize.class);
                } else {
                    tenantAuthorize = method.getDeclaringClass().getAnnotation(TenantAuthorize.class);
                }
                long[] tenantIdArr = tenantAuthorize.value();
                if (tenantIdArr == null || tenantIdArr.length == 0) return true;
                return Arrays.binarySearch(tenantIdArr, ((AccountAuthentication) authentication).getTenantId()) > -1;
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
