package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
import com.byakuya.boot.backend.exception.ValidationFailedException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = {"/{id}", "/me"})
    public User get(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        if (id == null) {
            id = authentication.getAccountId();
        }
        return userService.query(id).orElseThrow(RecordNotFoundException::new);
    }

    @PostMapping(path = "/change/password")
    public void changePassword(@RequestParam String oPass, @RequestParam String nPass, AccountAuthentication authentication) {
        userService.changePassword(authentication.getAccountId(), oPass, nPass);
    }
}
