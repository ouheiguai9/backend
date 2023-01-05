package com.byakuya.boot.backend.component;

import com.byakuya.boot.backend.component.tenant.Tenant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface TenantOwner {
    Tenant getTenant();

    void setTenant(Tenant tenant);

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    default Long getTenantId() {
        Tenant tenant = getTenant();
        return tenant == null ? null : tenant.getId();
    }

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    default void setTenantId(Long tenantId) {
        if (tenantId == null) return;
        Tenant tenant = getTenant();
        if (tenant == null) {
            tenant = new Tenant();
            setTenant(tenant);
        }
        tenant.setId(tenantId);
    }
}
