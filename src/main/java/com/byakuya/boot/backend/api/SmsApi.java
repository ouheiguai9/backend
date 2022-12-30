package com.byakuya.boot.backend.api;

import com.byakuya.boot.backend.component.captcha.CaptchaService;
import com.byakuya.boot.backend.component.captcha.Type;
import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.service.SpringService;
import com.byakuya.boot.backend.service.sms.ISmsService;
import com.byakuya.boot.backend.service.sms.SmsSender;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2022/12/21 22:18
 */
@ApiModule(path = "sms", secure = false)
@Validated
class SmsApi {
    private final CaptchaService captchaService;
    private final SpringService springService;
    private final UserService userService;

    SmsApi(CaptchaService captchaService, SpringService springService, UserService userService) {
        this.captchaService = captchaService;
        this.springService = springService;
        this.userService = userService;
    }

    @PostMapping("/login/captcha")
    public ResponseEntity<Boolean> loginCaptcha(@RequestParam SmsSender sender,
                                                @RequestParam Long tenantId,
                                                @RequestParam String to,
                                                @RequestParam String template,
                                                @RequestParam(required = false) String target,
                                                @RequestParam(required = false, defaultValue = "6") Integer length,
                                                @RequestParam(required = false, defaultValue = "3") Integer minutes,
                                                @RequestParam(required = false, defaultValue = "false") boolean addUser) {
        if (!StringUtils.hasText(target)) {
            target = to;
        }
        String captcha = captchaService.createNumberCaptcha(length);
        ISmsService smsService = springService.getSmsService(sender);
        smsService.sendLoginCaptcha(tenantId, to, template, captcha);
        if (addUser && !userService.loadByPhone(target, tenantId).isPresent()) {
            User user = new User();
            user.setTenantId(tenantId);
            user.setUsername(target);
            user.setPhone(target);
            user.setNickname(target);
            user.setPassword(captchaService.createCaptcha(8) + captchaService.createCaptcha(4));
            userService.add(user);
        }
        captchaService.add(tenantId, Type.LOGIN, target, captcha, LocalDateTime.now().plusMinutes(minutes), true);
        return ResponseEntity.ok(true);
    }
}
