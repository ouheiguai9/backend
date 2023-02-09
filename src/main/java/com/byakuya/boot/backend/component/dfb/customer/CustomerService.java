package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Created by 田伯光 at 2023/2/9 15:11
 */
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserService userService;

    public CustomerService(CustomerRepository customerRepository, UserService userService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
    }

    @Transactional
    public Optional<Customer> query(long accountId, boolean createIfNotExist) {
        Optional<Customer> rtnVal = customerRepository.findById(accountId);
        if (rtnVal.isPresent() || !createIfNotExist) return rtnVal;
        Optional<User> opt = userService.query(accountId);
        if (opt.isPresent()) {
            User user = opt.get();
            String phone = user.getPhone();
            if (StringUtils.hasText(phone) && user.getPhone().charAt(0) == 'C') {
                Customer customer = new Customer().setUser(user);
                customer.setPhone(phone.substring(1));
                return Optional.of(customerRepository.save(customer));
            }
        }
        return Optional.empty();
    }
}
