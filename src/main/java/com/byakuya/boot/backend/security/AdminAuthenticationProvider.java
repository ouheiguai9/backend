package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.Account;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by 田伯光 at 2022/8/23 20:28
 */
public class AdminAuthenticationProvider implements RequestAuthenticationProvider {
    @Override
    public Account authenticate(RequestAuthenticationToken token) throws AuthenticationException {
        return null;
    }

    @Override
    public String authKey() {
        return "admin";
    }
}
