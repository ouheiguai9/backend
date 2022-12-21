package com.byakuya.boot.backend.security;

import com.byakuya.boot.backend.component.account.AccountService;
import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by 田伯光 at 2022/10/12 23:56
 */
@Component
public class StandardAuthenticationProvider extends AbstractAccountAuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    public StandardAuthenticationProvider(AccountService accountService, PasswordEncoder passwordEncoder, UserService userService) {
        super(accountService);
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    protected AccountAuthentication retrieveAuthentication(HttpServletRequest request) throws AuthenticationException {
        String userType = getHeaderKey(request, "userType", "username");
        String userParam = getHeaderKey(request, "userParam", "user");
        Long tenantId = Long.valueOf(getHeaderKey(request, "tenantId", "0"));
        String userStr = getHeaderKey(request, userParam, "");
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
        return new AccountAuthentication(user.getTenantId(), user.getAccountId(), user.getNickname(), user.getPassword());
    }

    @Override
    protected void additionalAuthenticationChecks(AccountAuthentication auth, HttpServletRequest request) throws AuthenticationException {
        String passParam = getHeaderKey(request, "passParam", "pass");
        String passStr = getHeaderKey(request, passParam, "");
        if (!passwordEncoder.matches(passStr, auth.getCredentials().toString())) {
            throw new BadCredentialsException(passStr);
        }
    }

    @Override
    public String authKey() {
        return "standard";
    }
}
