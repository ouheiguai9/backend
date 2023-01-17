package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.AbstractAuditableEntity;
import com.byakuya.boot.backend.component.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "T_SYS_USER", indexes = {@Index(columnList = "username"), @Index(columnList = "email"), @Index(columnList = "phone")})
@Accessors(chain = true)
public class User extends AbstractAuditableEntity {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    @NotBlank(message = "error.validation.user.username.required")
    @Column(length = 50)
    private String username;
    @Column(nullable = false)
    private String password;
    @NotBlank(message = "error.validation.user.nickname.required")
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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id")
    @MapsId
    private Account account;

    @JsonIgnore
    public Long getAccountId() {
        return getId();
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public void setEmail(String email) {
        this.email = email;
    }
}
