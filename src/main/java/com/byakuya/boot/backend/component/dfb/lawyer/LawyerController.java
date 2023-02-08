package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by 田伯光 at 2023/2/8 22:54
 */
@ApiModule(path = "dfb/lawyers")
@Validated
class LawyerController {

    private final LawyerService lawyerService;

    LawyerController(LawyerService lawyerService) {
        this.lawyerService = lawyerService;
    }

    @GetMapping(path = {"", "/{id}"})
    public Lawyer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        return lawyerService.query(id != null ? id : authentication.getAccountId(), true).orElseThrow(() -> AuthException.forbidden(null));
    }
}
