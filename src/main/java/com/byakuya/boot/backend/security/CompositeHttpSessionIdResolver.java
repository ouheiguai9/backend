package com.byakuya.boot.backend.security;

import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author ganzl
 */
@Component
public class CompositeHttpSessionIdResolver implements HttpSessionIdResolver {
    private final CookieHttpSessionIdResolver cookie = new CookieHttpSessionIdResolver();
    private final HeaderHttpSessionIdResolver header = HeaderHttpSessionIdResolver.xAuthToken();

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        List<String> rtnVal = cookie.resolveSessionIds(request);
        if (rtnVal != null && rtnVal.size() > 0) {
            return rtnVal;
        }
        return header.resolveSessionIds(request);
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        cookie.setSessionId(request, response, sessionId);
        header.setSessionId(request, response, sessionId);
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        cookie.expireSession(request, response);
        header.expireSession(request, response);
    }
}
