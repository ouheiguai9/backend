package com.byakuya.boot.backend.component.organization;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import com.byakuya.boot.backend.component.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Created by 田伯光 at 2022/8/30 12:10
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_ORGANIZATION", indexes = {@Index(columnList = "parent,code", unique = true)})
@Accessors(chain = true)
public class Organization extends AbstractAuditableEntity<Account> {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    @JoinColumn(name = "parent")
    private Organization parent;
    @NotBlank
    @Column(nullable = false)
    private String code;
    @NotBlank
    @Column(nullable = false)
    private String name;
    private String logo;
    private String address;
    private int ordering;
    @Column(length = 1000)
    private String description;
    private int level;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "T_SYS_ORG_PATH",
            joinColumns = {@JoinColumn(name = "ancestor")},
            inverseJoinColumns = {@JoinColumn(name = "organization")})
    private Set<Organization> ancestors;
    @JsonIgnore
    @ManyToMany(mappedBy = "ancestors")
    private Set<Organization> descendants;
}
