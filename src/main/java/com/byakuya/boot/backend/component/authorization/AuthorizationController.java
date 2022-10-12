package com.byakuya.boot.backend.component.authorization;

import com.byakuya.boot.backend.config.ApiModule;
import org.springframework.web.bind.annotation.GetMapping;

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
}
