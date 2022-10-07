package com.byakuya.boot.backend.component.role;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.component.organization.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "T_SYS_ROLE", indexes = {@Index(columnList = "company,code", unique = true)})
@Accessors(chain = true)
public class Role extends AbstractAuditableEntity<Account> {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @NotBlank
    @Column(length = 50, nullable = false)
    private String code;
    @NotBlank
    @Column(length = 100, nullable = false)
    private String name;
    private String description;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company", nullable = false)
    private Organization company;

    @JsonProperty
    public Long getCompanyId() {
        return company != null ? company.getId() : null;
    }

    @JsonProperty
    public Role setCompanyId(Long companyId) {
        if (companyId != null) {
            Organization company = new Organization();
            company.setId(companyId);
            this.company = company;
        }
        return this;
    }
}
