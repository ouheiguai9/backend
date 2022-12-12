package com.byakuya.boot.backend.component.captcha;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.tenant.Tenant;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by 田伯光 at 2022/9/12 20:54
 */
@Data
@Entity
@Table(name = "T_SYS_CAPTCHA")
@IdClass(CaptchaId.class)
@Accessors(chain = true)
class Captcha implements Persistable<CaptchaId>, Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Transient
    private boolean isNew;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant", nullable = false)
    private Tenant tenant;
    @Id
    @Column(nullable = false)
    private String target;
    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type captchaType;
    @Column(nullable = false)
    private String value;
    @Column(nullable = false)
    private LocalDateTime start;
    @Column(nullable = false)
    private LocalDateTime end;
    private boolean valid;

    public Captcha setTenantId(Long tenantId) {
        if (Objects.nonNull(tenantId)) {
            tenant = new Tenant();
            tenant.setId(tenantId);
        }
        return this;
    }

    @Override
    public CaptchaId getId() {
        return new CaptchaId().setTenant(tenant).setTarget(target).setCaptchaType(captchaType);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
