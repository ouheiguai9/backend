package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.component.organization.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2022/8/30 12:12
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_COMMON_USER")
public class User extends AbstractAuditableEntity<Account> {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @NotBlank
    @Column(nullable = false)
    private String username;
    @NotBlank
    @Column(nullable = false)
    private String password;
    @NotBlank
    @Column(nullable = false)
    private String nickname;
    @NotBlank
    @Column(nullable = false)
    private String phone;
    @NotBlank
    @Column(nullable = false)
    private String email;
    private String address;
    private String avatar;
    private LocalDateTime beginValidPeriod = LocalDateTime.now();
    private LocalDateTime endValidPeriod;
    private LocalDateTime lastPasswordModifiedDate;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system", updatable = false, nullable = false)
    private Organization system;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account", updatable = false, unique = true, nullable = false)
    private Account account;
}
