package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.dfb.CoreService;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
