package com.byakuya.boot.backend.api;

import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.config.ApiModule;
import org.springframework.validation.annotation.Validated;

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
}
