package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
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
    private final CustomerRepository customerRepository;

    CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping(path = {"", "/{id}"})
    public Customer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        if (AccountAuthentication.isAdmin(authentication)) {
            throw new RecordNotFoundException();
        }
        Long customerId = id != null ? id : authentication.getAccountId();
        return customerRepository.findById(customerId).orElseGet(() -> {
            Customer customer = new Customer().setAccount(authentication.getAccount());
            customerRepository.save(customer);
            return customer;
        });
    }
}
