package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import com.byakuya.boot.backend.vo.KeyValueVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/states")
    public List<KeyValueVO<String, String>> read() {
        return Arrays.stream(LawyerState.values()).map(state -> KeyValueVO.of(state.toString(), state.text)).collect(Collectors.toList());
    }

    @AclApiMethod(value = "lawyer_list", desc = "律师列表", method = RequestMethod.GET)
    public Page<Lawyer> getCommentList(@PageableDefault(sort = {"user.account.createTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                       @RequestParam(value = "name", required = false) String nameLike,
                                       @RequestParam(value = "phone", required = false) String phoneLike,
                                       @RequestParam(value = "state", required = false) LawyerState[] stateIn,
                                       @RequestParam(value = "key", required = false) String[] keyIn,
                                       @RequestParam(value = "createTime", required = false) LocalDateTime[] createTimeIn) {
        return lawyerService.query(pageable, nameLike, phoneLike, stateIn, keyIn, createTimeIn);
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
