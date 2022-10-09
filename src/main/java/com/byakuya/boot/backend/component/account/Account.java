package com.byakuya.boot.backend.component.account;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractBaseEntity;
import com.byakuya.boot.backend.component.role.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by 田伯光 at 2022/8/21 9:09
 */
@Data
@Entity
@Table(name = "T_SYS_ACCOUNT")
@Accessors(chain = true)
public class Account extends AbstractBaseEntity {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Id
    @GeneratedValue(generator = "snowflake_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private int loginErrorCount;
    private LocalDateTime loginErrorTime;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "T_SYS_USER_ROLE",
            joinColumns = {@JoinColumn(name = "role")},
            inverseJoinColumns = {@JoinColumn(name = "user")})
    private Set<Role> roles;
}
