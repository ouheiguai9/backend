package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.Account;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by 田伯光 at 2022/4/28 11:59
 */
public interface RequestAuthenticationProvider {
    Account authenticate(RequestAuthenticationToken token) throws AuthenticationException;

    String authKey();
}
