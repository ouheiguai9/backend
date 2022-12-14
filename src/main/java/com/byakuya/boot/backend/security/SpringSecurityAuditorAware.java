package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.Account;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by 田伯光 on 2020/12/17.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<Account> {

    @Override
    public Optional<Account> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(x -> x.isAuthenticated() && x instanceof AccountAuthentication)
                .map(AccountAuthentication.class::cast)
                .map(AccountAuthentication::getAccount);
    }
}
