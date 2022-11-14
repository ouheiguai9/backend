package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.ValidationFailedException;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by 田伯光 at 2022/10/13 23:48
 */
@AclApiModule(path = "users", value = "user", desc = "用户管理")
@Validated
class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @AclApiMethod(value = "add", desc = "增加", method = RequestMethod.POST)
    public User create(@Valid @RequestBody User user) {
        if (!StringUtils.hasText(user.getPassword())) {
            throw ValidationFailedException.buildWithCode("error.validation.user.password.required");
        }
        return userService.add(user);
    }
}
