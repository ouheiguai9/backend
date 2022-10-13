package com.byakuya.boot.backend.component.authorization;

import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by 田伯光 at 2022/10/12 2:47
 */
@ApiModule(path = "authorizations")
class AuthorizationController {
    private final AuthorizationService authorizationService;

    AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("acl")
    public List<ApiResourceVO> acl() {
        return authorizationService.aclAll();
    }

    @RequestMapping(value = "me", method = {RequestMethod.POST, RequestMethod.GET})
    public Authentication me(AccountAuthentication authentication) {
        return authentication;
    }
}
