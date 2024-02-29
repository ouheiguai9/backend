package com.byakuya.boot.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

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
