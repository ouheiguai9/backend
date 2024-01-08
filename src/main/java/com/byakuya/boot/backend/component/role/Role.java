package com.byakuya.boot.backend.component.role;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;


/**
 * Created by 田伯光 at 2022/10/4 23:32
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_ROLE", indexes = {@Index(columnList = "tenant_id,name", unique = true)})
@Accessors(chain = true)
public class Role extends AbstractAuditableEntity {
    @Serial
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @NotBlank(message = "error.validation.role.name.required")
    @Column(nullable = false)
    private String name;
    private String description;
}
