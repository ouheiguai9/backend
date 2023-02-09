package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.component.dfb.CoreService;
import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by 田伯光 at 2023/2/8 22:54
 */
@AclApiModule(path = "dfb/lawyers", value = "dfb_lawyer", desc = "律师管理")
@Validated
class LawyerController {

    private final LawyerService lawyerService;
    private final CoreService coreService;

    LawyerController(LawyerService lawyerService, CoreService coreService) {
        this.lawyerService = lawyerService;
        this.coreService = coreService;
    }

    @GetMapping(path = {"", "/{id}"})
    public Lawyer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        return lawyerService.query(id != null ? id : authentication.getAccountId(), true).orElseThrow(() -> AuthException.forbidden(null));
    }

    @PostMapping("/duty/on")
    public void dutyOn(AccountAuthentication authentication) {
        coreService.addCandidateLawyer(lawyerService.dutyOn(authentication.getAccountId()));
    }

    @PostMapping("/duty/off")
    public void dutyOff(AccountAuthentication authentication) {
        coreService.removeCandidateLawyer(lawyerService.dutyOff(authentication.getAccountId()));
    }

    @AclApiMethod(value = "approve", desc = "审核", path = "/approve/{id}", method = RequestMethod.POST)
    public void approve(@PathVariable Long id) {
        lawyerService.approve(id);
    }
}
