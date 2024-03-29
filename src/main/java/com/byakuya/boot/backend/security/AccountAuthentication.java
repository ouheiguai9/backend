package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.account.Account;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by 田伯光 at 2022/8/28 11:50
 */
@JsonDeserialize()
public class AccountAuthentication implements Authentication, CredentialsContainer {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Getter
    private final long accountId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Getter
    private final long tenantId;
    @JsonIgnore
    private Object credentials;
    @Getter
    @Setter
    private boolean tenantAdmin = false;
    private String name;
    private Map<String, Serializable> details;
    @Getter
    private Set<String> apis;

    /**
     * 用于Json反序列化
     */
    private AccountAuthentication() {
        this.tenantId = 0;
        this.accountId = 0;
    }

    public AccountAuthentication(long tenantId, long accountId, String name, Object credentials) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException();
        }
        this.tenantId = tenantId;
        this.accountId = accountId;
        this.name = name;
        this.credentials = credentials;
        this.setApis(null);
        this.setDetails(null);
    }

    public static boolean isAdmin(Authentication authentication) {
        return authentication instanceof Admin;
    }

    public AccountAuthentication setApis(Set<String> apis) {
        if (apis == null || apis.isEmpty()) {
            this.apis = Collections.emptySet();
        } else {
            this.apis = Collections.unmodifiableSet(apis);
        }
        return this;
    }

    public AccountAuthentication copyAndModifyName(String name) {
        AccountAuthentication copy = new AccountAuthentication(this.tenantId, this.accountId, name, this.credentials);
        copy.apis = this.apis;
        return copy;
    }

    public boolean hasApiAuth(String api) {
        return this.apis.contains(api);
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return apis.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
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
        account.setTenantId(tenantId);
        return account;
    }

    @Override
    public void eraseCredentials() {
        this.credentials = null;
    }

    static final class Admin extends AccountAuthentication {
        static final Admin instance = new Admin();
        private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

        private Admin() {
            super(0L, 0L, "超级管理员", null);
        }


        @JsonIgnore
        @Override
        public Set<String> getApis() {
            return super.getApis();
        }

        @Override
        public AccountAuthentication copyAndModifyName(String name) {
            return this;
        }

        @JsonIgnore
        @Override
        public Object getDetails() {
            return super.getDetails();
        }

        @Override
        public Account getAccount() {
            return null;
        }
    }
}
