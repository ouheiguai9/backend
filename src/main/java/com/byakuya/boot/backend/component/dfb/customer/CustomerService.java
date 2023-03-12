package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.byakuya.boot.backend.component.dfb.ConstantUtils.INVALID_CUSTOMER_PREFIX;

/**
 * Created by 田伯光 at 2023/2/9 15:11
 */
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    public CustomerService(CustomerRepository customerRepository, UserService userService, StringRedisTemplate stringRedisTemplate) {
        this.customerRepository = customerRepository;
        this.userService = userService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public Page<Customer> query(Pageable pageable, String phoneLike) {
        if (StringUtils.hasText(phoneLike)) {
            return customerRepository.findAllByPhoneContains(pageable, phoneLike);
        } else {
            return customerRepository.findAll(pageable);
        }
    }

    @Transactional
    public Optional<Customer> query(Long accountId, boolean createIfNotExist) {
        if (!createIfNotExist && checkInvalid(accountId)) {
            return Optional.empty();
        }
        Optional<Customer> rtnVal = customerRepository.findById(accountId);
        if (!rtnVal.isPresent() && !createIfNotExist) {
            stringRedisTemplate.opsForValue().set(INVALID_CUSTOMER_PREFIX + accountId, "true", 10, TimeUnit.SECONDS);
        }
        if (rtnVal.isPresent() || !createIfNotExist) return rtnVal;
        Optional<User> opt = userService.query(accountId);
        if (opt.isPresent()) {
            User user = opt.get();
            String phone = user.getPhone();
            if (StringUtils.hasText(phone) && user.getPhone().charAt(0) == 'C') {
                stringRedisTemplate.delete(INVALID_CUSTOMER_PREFIX + accountId);
                Customer customer = new Customer().setUser(user);
                customer.setPhone(phone.substring(1));
                return Optional.of(customerRepository.save(customer));
            }
        }
        return Optional.empty();
    }

    public boolean checkInvalid(Long customerId) {
        return stringRedisTemplate.opsForValue().getAndExpire(INVALID_CUSTOMER_PREFIX + customerId, 10, TimeUnit.SECONDS) != null;
    }
}
