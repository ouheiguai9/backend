package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.dfb.CoreService;
import com.byakuya.boot.backend.component.dfb.order.Evaluation;
import com.byakuya.boot.backend.component.dfb.order.Order;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 田伯光 at 2023/1/5 22:54
 */
@ApiModule(path = "dfb/customers")
@Validated
class CustomerController {
    private final CustomerService customerService;
    private final CoreService coreService;

    CustomerController(CustomerService customerService, CoreService coreService) {
        this.customerService = customerService;
        this.coreService = coreService;
    }


    @GetMapping(path = {"", "/{id}"})
    public Customer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        return customerService.query(id != null ? id : authentication.getAccountId(), true).orElseThrow(() -> AuthException.forbidden(null));
    }

    @PostMapping("/call")
    public String read(@RequestParam(required = false, defaultValue = "") String exclude, AccountAuthentication authentication) {
        return coreService.call(authentication.getAccountId(), exclude);
    }

    @PostMapping("/evaluation")
    public Evaluation evaluation(@RequestBody Evaluation evaluation, AccountAuthentication authentication) {
        if (evaluation.getOrderId() == null && !authentication.isTenantAdmin()) {
            //虚拟评价只能租户管理员能添加
            throw AuthException.forbidden(null);
        }
        return coreService.getOrderService().addEvaluation(evaluation, authentication.getAccountId());
    }

    @GetMapping("/orders")
    public Page<Order> read(@PageableDefault Pageable pageable, String search) {
        return null;
    }
}
