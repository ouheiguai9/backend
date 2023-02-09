package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by 田伯光 at 2023/1/5 22:54
 */
@ApiModule(path = "dfb/customers")
@Validated
class CustomerController {
    private final CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping(path = {"", "/{id}"})
    public Customer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        return customerService.query(id != null ? id : authentication.getAccountId(), true).orElseThrow(() -> AuthException.forbidden(null));
    }
}
