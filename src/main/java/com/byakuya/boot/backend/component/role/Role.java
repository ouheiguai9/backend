package com.byakuya.boot.backend.component.role;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * Created by 田伯光 at 2022/10/4 23:32
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_ROLE", indexes = {@Index(columnList = "tenant,name", unique = true)})
@Accessors(chain = true)
public class Role extends AbstractBaseEntity {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @GeneratedValue(generator = "snowflake_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String name;
    private String description;
}
