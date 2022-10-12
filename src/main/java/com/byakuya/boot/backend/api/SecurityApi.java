package com.byakuya.boot.backend.api;

import com.byakuya.boot.backend.component.role.Role;
import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by 田伯光 at 2022/8/24 14:15
 */
@ApiModule(path = "security", secure = false)
@Validated
class SecurityApi {
    private final UserService userService;

    public SecurityApi(UserService userService) {
        this.userService = userService;
    }

    @AclApiMethod(value = "add", desc = "增加", path = "", method = RequestMethod.POST)
    public ResponseEntity<User> create(@Valid @RequestBody Role role, AccountAuthentication authentication) {
        return ResponseEntity.ok(userService.createByUsername(null, null, null));
    }
}
