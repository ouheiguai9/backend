package com.byakuya.boot.backend.component.organization;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Created by 田伯光 at 2022/8/30 12:10
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_ORGANIZATION", indexes = {@Index(columnList = "parent,name", unique = true)})
@Accessors(chain = true)
public class Organization extends AbstractAuditableEntity {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent", updatable = false)
    private Organization parent;
    @NotBlank
    @Column(nullable = false)
    private String name;
    private String logo;
    private String address;
    private int ordering;
    @Column(length = 1000)
    private String description;
    @Column(updatable = false)
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

    @JsonProperty
    public Organization setParent(Organization parent) {
        this.parent = parent;
        return this;
    }

    @JsonProperty
    public Long getParentId() {
        return parent != null ? parent.getId() : null;
    }
}
