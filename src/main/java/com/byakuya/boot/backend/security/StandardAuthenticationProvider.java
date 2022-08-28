package com.byakuya.boot.backend.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by ganzl at 2022/4/28 15:34
 */
@Component
public class StandardAuthenticationProvider implements RequestAuthenticationProvider {
    //    private final PasswordEncoder passwordEncoder;
//    private final UserService userService;
    private String usernameParameter = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
    private String passwordParameter = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;


//    public StandardAuthenticationProvider(PasswordEncoder passwordEncoder, UserService userService) {
//        this.passwordEncoder = passwordEncoder;
//        this.userService = userService;
//    }
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
        return null;
    }

    @Override
    public String authKey() {
        return "standard";
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }
}
