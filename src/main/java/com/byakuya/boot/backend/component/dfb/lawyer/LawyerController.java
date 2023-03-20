package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by 田伯光 at 2023/2/8 22:54
 */
@AclApiModule(path = "dfb/lawyers", value = "dfb_lawyer", desc = "律师管理")
@Validated
class LawyerController {

    private final LawyerService lawyerService;

    LawyerController(LawyerService lawyerService) {
        this.lawyerService = lawyerService;
    }

    @GetMapping(path = {"/me", "/{id}"})
    public Lawyer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        return lawyerService.query(id != null ? id : authentication.getAccountId(), true).orElseThrow(() -> AuthException.forbidden(null));
    }

    @AclApiMethod(value = "lawyer_stat", desc = "律师统计", path = "/stat", method = RequestMethod.GET)
    public List<Lawyer> stat(@RequestParam(value = "createTime", required = false) LocalDateTime[] createTimeIn) {
        if (createTimeIn == null || createTimeIn.length < 2) {
            LocalDateTime now = LocalDateTime.now();
            createTimeIn = new LocalDateTime[]{now.minusDays(7), now};
        }
        return lawyerService.queryAllStat(createTimeIn[0], createTimeIn[1]);
    }

    @PostMapping("/submit")
    public Lawyer submit(@RequestBody Lawyer lawyer, AccountAuthentication authentication) {
        if (!Long.valueOf(authentication.getAccountId()).equals(lawyer.getId())) {
            throw AuthException.forbidden(null);
        }
        return lawyerService.submitInfo(lawyer);
    }

    @PostMapping("/duty/on")
    public void dutyOn(AccountAuthentication authentication) {
        lawyerService.dutyOn(authentication.getAccountId());
    }

    @PostMapping("/duty/off")
    public void dutyOff(AccountAuthentication authentication) {
        lawyerService.dutyOff(authentication.getAccountId());
    }

    @AclApiMethod(value = "approve", desc = "审核", path = "/approve/{id}", method = RequestMethod.POST)
    public void approve(@PathVariable Long id) {
        lawyerService.approve(id);
    }
}
