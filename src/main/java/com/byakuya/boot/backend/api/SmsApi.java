package com.byakuya.boot.backend.api;

import com.byakuya.boot.backend.component.captcha.CaptchaService;
import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.service.SubMailService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by 田伯光 at 2022/12/21 22:18
 */
@ApiModule(path = "sms", secure = false)
@Validated
class SmsApi {
    private final CaptchaService captchaService;
    private final SubMailService subMailService;
    private final UserService userService;

    SmsApi(CaptchaService captchaService, SubMailService subMailService, UserService userService) {
        this.captchaService = captchaService;
        this.subMailService = subMailService;
        this.userService = userService;
    }

    @PostMapping("/login/captcha")
    public void loginCaptcha(@RequestParam Long tenantId,
                             @RequestParam String to,
                             @RequestParam(required = false) String target,
                             @RequestParam(required = false, defaultValue = "false") boolean addUser) {
        if (!StringUtils.hasText(target)) {
            target = to;
        }
        System.out.println(tenantId);
    }
}
