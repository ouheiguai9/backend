package com.byakuya.boot.backend.api;

import com.byakuya.boot.backend.config.ApiModule;
import org.springframework.validation.annotation.Validated;

/**
 * Created by 田伯光 at 2022/8/24 14:15
 */
@ApiModule(path = "users", secure = false)
@Validated
class UserApi {
//    private final UserService userService;
//
//    public UserApi(UserService userService) {
//        this.userService = userService;
//    }
}
