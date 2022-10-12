package com.byakuya.boot.backend.component.parameter;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * Created by 田伯光 at 2022/10/10 1:43
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_PARAMETER", indexes = {@Index(columnList = "groupKey,itemKey")})
@Accessors(chain = true)
@AssociationOverride(name = "tenant", joinColumns = @JoinColumn(name = "tenant", updatable = false))
public class Parameter extends AbstractAuditableEntity {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    @NotBlank
    @Column(nullable = false)
    private String groupKey;

    @NotBlank
    @Column(nullable = false)
    private String itemKey;

    @NotBlank
    @Column(nullable = false)
    private String itemValue;

    private int ordering;
    private String description;

    @Override
    protected boolean acceptNullTenant() {
        return false;
    }
}
