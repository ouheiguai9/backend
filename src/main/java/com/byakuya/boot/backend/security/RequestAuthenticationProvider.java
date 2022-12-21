package com.byakuya.boot.backend.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 田伯光 at 2022/4/28 11:59
 */
public interface RequestAuthenticationProvider {
    AccountAuthentication authenticate(RequestAuthenticationToken token) throws AuthenticationException;

    String authKey();

    default String getHeaderKey(HttpServletRequest request, String key, String defaultValue) {
        String rtnVal = request.getHeader(key);
        if (StringUtils.hasText(rtnVal)) {
            rtnVal = rtnVal.trim();
        } else {
            rtnVal = defaultValue;
        }
        return rtnVal;
    }
}
