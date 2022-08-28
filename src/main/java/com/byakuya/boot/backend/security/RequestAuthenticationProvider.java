package com.byakuya.boot.backend.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by 田伯光 at 2022/4/28 11:59
 */
public interface RequestAuthenticationProvider {
    AccountAuthentication authenticate(RequestAuthenticationToken token) throws AuthenticationException;

    String authKey();
}
