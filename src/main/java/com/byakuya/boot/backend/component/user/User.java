package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import com.byakuya.boot.backend.component.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2022/8/30 12:12
 */
@Setter
@Getter
@Entity
@Table(name = "T_SYS_USER")
@Accessors(chain = true)
public class User extends AbstractAuditableEntity<Account> {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Column(length = 50)
    private String username;
    @Column(length = 128)
    private String password;
    @NotBlank
    @Column(nullable = false)
    private String nickname;
    @Column(length = 20)
    private String phone;
    @Column(length = 60)
    private String email;
    private String address;
    private String avatar;
    private LocalDateTime beginValidPeriod = LocalDateTime.now();
    private LocalDateTime endValidPeriod;
    private LocalDateTime lastPasswordModifiedDate;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account", updatable = false, unique = true, nullable = false)
    private Account account;
}
