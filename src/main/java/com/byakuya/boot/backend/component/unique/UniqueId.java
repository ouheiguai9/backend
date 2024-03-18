package com.byakuya.boot.backend.component.unique;

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
public class UniqueId implements TenantOwner, Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private Tenant tenant;
    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @Accessors(chain = true)
    private Type uniqueType;
    @Column(nullable = false, updatable = false)
    @Accessors(chain = true)
    private String uniqueValue;

    public UniqueId() {

    }

    public UniqueId(Type uniqueType, Long tenantId, String uniqueValue) {
        this.uniqueType = uniqueType;
        this.uniqueValue = uniqueValue;
        this.setTenantId(tenantId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniqueId uniqueId = (UniqueId) o;
        if (!Objects.equals(getTenantId(), uniqueId.getTenantId())) return false;
        if (!uniqueValue.equals(uniqueId.uniqueValue)) return false;
        return uniqueType == uniqueId.uniqueType;
    }

    @Override
    public int hashCode() {
        int result = getTenantId().hashCode();
        result = 31 * result + uniqueValue.hashCode();
        result = 31 * result + uniqueType.hashCode();
        return result;
    }
}
