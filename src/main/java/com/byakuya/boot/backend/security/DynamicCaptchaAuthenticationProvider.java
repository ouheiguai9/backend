package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.AccountService;
import com.byakuya.boot.backend.component.captcha.CaptchaService;
import com.byakuya.boot.backend.component.captcha.Type;
import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.exception.ValidationFailedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by 田伯光 at 2022/12/14 16:09
 */
@Component
public class DynamicCaptchaAuthenticationProvider extends AbstractAccountAuthenticationProvider {
    private final UserService userService;
    private final CaptchaService captchaService;

    public DynamicCaptchaAuthenticationProvider(AccountService accountService, UserService userService, CaptchaService captchaService) {
        super(accountService);
        this.userService = userService;
        this.captchaService = captchaService;
    }

    @Override
    protected AccountAuthentication retrieveAuthentication(HttpServletRequest request) throws AuthenticationException {
        String targetType = getHeaderKey(request, "targetType", "phone");
        Long tenantId = Long.valueOf(getHeaderKey(request, "tenantId", "0"));
        String target = getTarget(request);
        Optional<User> opt = Optional.empty();
        switch (targetType) {
            case "phone":
                opt = userService.loadByPhone(target, tenantId);
                break;
            case "email":
                opt = userService.loadByEmail(target, tenantId);
                break;
            case "username":
                opt = userService.loadByUsername(target, tenantId);
                break;

        }
        User user = opt.orElseThrow(() -> new UsernameNotFoundException(target));
        return new AccountAuthentication(user.getTenantId(), user.getAccountId(), user.getNickname(), null);
    }

    @Override
    protected void additionalAuthenticationChecks(AccountAuthentication auth, HttpServletRequest request) throws AuthenticationException {
        String target = getTarget(request);
        String captcha = getHeaderKey(request, "captcha", "");
        boolean ignoreCase = Boolean.parseBoolean(getHeaderKey(request, "ignoreCase", "true"));
        try {
            captchaService.check(auth.getTenantId(), Type.LOGIN, target, captcha, ignoreCase);
        } catch (ValidationFailedException t) {
            throw new BadCredentialsException(t.getMessage(), t);
        }
    }

    private String getTarget(HttpServletRequest request) {
        return getHeaderKey(request, "target", "");
    }

    @Override
    public String authKey() {
        return "dynamic-captcha";
    }
}
