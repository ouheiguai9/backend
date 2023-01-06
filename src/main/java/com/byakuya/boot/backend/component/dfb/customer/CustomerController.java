package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.config.TenantAuthorize;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

/**
 * Created by 田伯光 at 2023/1/5 22:54
 */
@ApiModule(path = "dfb/customers")
@TenantAuthorize(ConstantUtils.TENANT_ID_DFB)
@Validated
class CustomerController {
    private final CustomerRepository customerRepository;
    private final UserService userService;

    CustomerController(CustomerRepository customerRepository, UserService userService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
    }

    @GetMapping(path = {"", "/{id}"})
    @Transactional
    public Customer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        Long accountId = id != null ? id : authentication.getAccountId();
        return customerRepository.findById(accountId).orElseGet(() -> {
            Optional<User> opt = userService.query(accountId);
            if (opt.isPresent()) {
                User user = opt.get();
                String phone = user.getPhone();
                if (StringUtils.hasText(phone) && user.getPhone().charAt(0) == 'C') {
                    Customer customer = new Customer().setUser(user);
                    customer.setPhone(phone.substring(1));
                    return customerRepository.save(customer);
                }
            }
            throw AuthException.forbidden(null);
        });
    }
}
