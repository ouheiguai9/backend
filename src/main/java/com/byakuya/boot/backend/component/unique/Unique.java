package com.byakuya.boot.backend.component.unique;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.tenant.Tenant;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by 田伯光 at 2022/9/12 20:54
 */
@Data
@Entity
@Table(name = "T_SYS_TABLE_UNIQUE")
@IdClass(UniqueId.class)
@Accessors(chain = true)
class Unique implements Persistable<UniqueId>, Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Transient
    private boolean isNew = false;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant", nullable = false)
    private Tenant tenant;
    @Id
    @Enumerated(EnumType.STRING)
    private Type uniqueType;
    @Id
    private String uniqueValue;

    public Unique setTenantId(Long tenantId) {
        if (Objects.nonNull(tenantId)) {
            tenant = new Tenant();
            tenant.setId(tenantId);
        }
        return this;
    }

    @Override
    public UniqueId getId() {
        return new UniqueId().setTenant(tenant).setUniqueType(uniqueType).setUniqueValue(uniqueValue);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
