package com.byakuya.boot.backend.component;

import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.security.SpringSecurityAuditorAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import javax.persistence.PrePersist;
import java.util.Objects;

/**
 * Created by 田伯光 at 2022/10/9 19:05
 */
@Slf4j
@Configurable
public class TenantEntityListener {

    private SpringSecurityAuditorAware auditorAware;

    @PrePersist
    public void prePersist(Object target) {
        if (!(target instanceof AbstractBaseEntity)) return;
        AbstractBaseEntity obj = (AbstractBaseEntity) target;
        auditorAware.getCurrentAuditor().ifPresent(account -> obj.setTenantId(account.getTenantId()));
        if (Objects.isNull(obj.getTenantId()) && !obj.acceptNullTenant()) {
            throw new BackendException(ErrorStatus.TENANT_NOT_EXIST);
        }
    }

    @Autowired
    public void setAuditorAware(SpringSecurityAuditorAware auditorAware) {
        Assert.notNull(auditorAware, "auditorAware must not be null!");
        this.auditorAware = auditorAware;
    }
}
