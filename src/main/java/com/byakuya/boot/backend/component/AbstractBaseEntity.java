package com.byakuya.boot.backend.component;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.tenant.Tenant;
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
public abstract class AbstractBaseEntity implements TenantOwner, Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
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

    public boolean acceptNullTenant() {
        return false;
    }
}
