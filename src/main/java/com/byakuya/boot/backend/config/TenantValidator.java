package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.component.AbstractBaseEntity;
import com.byakuya.boot.backend.security.SpringSecurityAuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * Created by 田伯光 at 2022/10/22 22:20
 */
@Component
public class TenantValidator implements Validator {
    private final SpringSecurityAuditorAware springSecurityAuditorAware;

    public TenantValidator(SpringSecurityAuditorAware springSecurityAuditorAware) {
        this.springSecurityAuditorAware = springSecurityAuditorAware;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!(target instanceof AbstractBaseEntity)) return;
        AbstractBaseEntity obj = (AbstractBaseEntity) target;
        springSecurityAuditorAware.getCurrentAuditor().ifPresent(account -> obj.setTenantId(account.getTenantId()));
        if (Objects.isNull(obj.getTenantId()) && !obj.acceptNullTenant()) {
            errors.reject("error.validation.tenant.required");
        }
    }
}
