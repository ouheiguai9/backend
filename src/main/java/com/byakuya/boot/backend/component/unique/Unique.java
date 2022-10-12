package com.byakuya.boot.backend.component.unique;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.tenant.Tenant;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by 田伯光 at 2022/9/12 20:54
 */
@Data
@Entity
@Table(name = "T_SYS_TABLE_UNIQUE", indexes = {@Index(columnList = "tenant,uniqueType,uniqueValue", unique = true)})
@Accessors(chain = true)
class Unique implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @GeneratedValue(generator = "table_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant", nullable = false)
    private Tenant tenant;
    @Enumerated(EnumType.STRING)
    private Type uniqueType;
    private String uniqueValue;

    public Unique setTenantId(Long tenantId) {
        if (Objects.nonNull(tenantId)) {
            tenant = new Tenant();
            tenant.setId(tenantId);
        }
        return this;
    }
}
