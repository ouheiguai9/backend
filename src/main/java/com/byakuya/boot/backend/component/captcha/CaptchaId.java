package com.byakuya.boot.backend.component.captcha;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.TenantOwner;
import com.byakuya.boot.backend.component.tenant.Tenant;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by 田伯光 at 2022/10/17 16:12
 */
@Getter
@Setter
@Embeddable
public class CaptchaId implements TenantOwner, Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private Tenant tenant;
    @Column(nullable = false, updatable = false)
    @Accessors(chain = true)
    private String target;
    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @Accessors(chain = true)
    private Type captchaType;

    public CaptchaId() {

    }

    public CaptchaId(Type captchaType, Long tenantId, String target) {
        this.captchaType = captchaType;
        this.target = target;
        this.setTenantId(tenantId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaptchaId captchaId = (CaptchaId) o;
        if (!Objects.equals(getTenantId(), captchaId.getTenantId())) return false;
        if (!target.equals(captchaId.target)) return false;
        return captchaType == captchaId.captchaType;
    }

    @Override
    public int hashCode() {
        int result = getTenantId().hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + captchaType.hashCode();
        return result;
    }
}
