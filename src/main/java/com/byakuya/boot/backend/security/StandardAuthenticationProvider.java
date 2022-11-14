package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by 田伯光 at 2022/10/12 23:56
 */
@Component
public class StandardAuthenticationProvider implements RequestAuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    public StandardAuthenticationProvider(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }
//
//    @Override
//    protected void postCheck(RequestAuthenticationToken token, User user, Serializable details) {
//        String presentedPassword = token.getRequest().getParameter(passwordParameter);
//        if (!passwordEncoder.matches(presentedPassword, user.getPassword())) {
//            throw new BadCredentialsException(getMessageService().message("ERR-10402"));
//        }
//    }


    @Override
    public AccountAuthentication authenticate(RequestAuthenticationToken token) throws AuthenticationException {
        HttpServletRequest request = token.getRequest();
        String userType = getHeaderKey(request, "userType", "username");
        String userParam = getHeaderKey(request, "userParam", "user");
        String passParam = getHeaderKey(request, "passParam", "pass");
        Long tenantId = Long.valueOf(getHeaderKey(request, "tenantId", "0"));
        String userStr = getHeaderKey(request, userParam, "");
        String passStr = getHeaderKey(request, passParam, "");
        Optional<User> opt = Optional.empty();
        switch (userType) {
            case "username":
                opt = userService.loadByUsername(userStr, tenantId);
                break;
            case "phone":
                opt = userService.loadByPhone(userStr, tenantId);
                break;
            case "email":
                opt = userService.loadByEmail(userStr, tenantId);
                break;
        }
        User user = opt.orElseThrow(() -> new UsernameNotFoundException(userStr));
        if (!passwordEncoder.matches(passStr, user.getPassword())) {
            throw new BadCredentialsException(passStr);
        }
        return new AccountAuthentication(user.getTenantId(), user.getAccountId(), user.getNickname());
    }

    @Override
    public String authKey() {
        return "standard";
    }

    private String getHeaderKey(HttpServletRequest request, String key, String defaultValue) {
        String rtnVal = request.getHeader(key);
        if (StringUtils.hasText(rtnVal)) {
            rtnVal = rtnVal.trim();
        } else {
            rtnVal = defaultValue;
        }
        return rtnVal;
    }
}
