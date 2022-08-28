package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.SystemVersion;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * Created by 田伯光 at 2022/4/24 20:49
 */
public class RequestAuthenticationToken implements Authentication {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    private HttpServletRequest request;

    public RequestAuthenticationToken(HttpServletRequest request) {
        Assert.notNull(request, "Request is null!");
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return request;
    }

    @Override
    public Object getDetails() {
        return request;
    }

    @Override
    public Object getPrincipal() {
        return request;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof RequestAuthenticationToken)) return false;
        return this.request.equals(((RequestAuthenticationToken) another).getRequest());
    }

    @Override
    public String toString() {
        return request.toString();
    }

    @Override
    public int hashCode() {
        return request.hashCode();
    }

    @Override
    public String getName() {
        return null;
    }
}
