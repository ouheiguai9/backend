package com.byakuya.boot.backend.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 田伯光 at 2023/1/7 23:39
 */
public interface TenantPrefixMatcher {
    Long matches(HttpServletRequest request);
}
