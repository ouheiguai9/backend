package com.byakuya.boot.backend.component.role;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * Created by 田伯光 at 2022/10/4 23:32
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_ROLE", indexes = {@Index(columnList = "tenant,name", unique = true)})
@Accessors(chain = true)
public class Role extends AbstractAuditableEntity {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @NotBlank(message = "error.validation.role.name.required")
    @Column(nullable = false)
    private String name;
    private String description;
}
