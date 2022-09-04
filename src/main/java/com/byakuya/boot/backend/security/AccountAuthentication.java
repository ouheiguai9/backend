package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by 田伯光 at 2022/8/28 11:50
 */
public class AccountAuthentication implements Authentication {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @Getter
    private final long accountId;
    private String name;
    private Map<String, Serializable> details;
    private Set<? extends GrantedAuthority> authorities;

    public AccountAuthentication(long accountId, String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException();
        }
        this.accountId = accountId;
        this.name = name;
        this.setAuthorities(null);
        this.setDetails(null);
    }

    public static boolean isAdmin(Authentication authentication) {
        return authentication instanceof Admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public AccountAuthentication setAuthorities(Set<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            this.authorities = Collections.emptySet();
        } else {
            this.authorities = Collections.unmodifiableSet(authorities);
        }
        return this;
    }

    @JsonIgnore
    @Override
    public Object getCredentials() {
        return details;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    public AccountAuthentication setDetails(Map<String, Serializable> details) {
        if (details == null || details.isEmpty()) {
            this.details = Collections.emptyMap();
        } else {
            this.details = Collections.unmodifiableMap(details);
        }
        return this;
    }

    @JsonIgnore
    @Override
    public Object getPrincipal() {
        return accountId;
    }

    @JsonIgnore
    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return name;
    }

    public AccountAuthentication setName(String name) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
        return this;
    }

    @JsonIgnore
    public Account getAccount() {
        Account account = new Account();
        account.setId(accountId);
        return account;
    }

    static final class Admin extends AccountAuthentication {
        static final Admin instance = new Admin();
        private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

        private Admin() {
            super(0L, "超级管理员");
        }

        @Override
        public Account getAccount() {
            return null;
        }
    }
}
