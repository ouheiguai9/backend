package com.byakuya.boot.backend.component;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.tenant.Tenant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/10/10 1:43
 */
@MappedSuperclass
//@EntityListeners(TenantEntityListener.class)
public abstract class AbstractBaseEntity implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant", nullable = false, updatable = false)
    private Tenant tenant;
    @Setter
    @Getter
    private boolean locked = false;
    @Setter
    @Getter
    @JsonIgnore
    private boolean deleted = false;

    @JsonIgnore
    public Tenant getTenant() {
        return tenant;
    }

    @JsonProperty
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Long getTenantId() {
        return tenant == null ? null : tenant.getId();
    }

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public void setTenantId(Long tenantId) {
        if (tenant == null) {
            tenant = new Tenant();
        }
        tenant.setId(tenantId);
    }

    public boolean acceptNullTenant() {
        return false;
    }
}
