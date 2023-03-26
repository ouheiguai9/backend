package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.dfb.lawyer.Lawyer;
import com.byakuya.boot.backend.component.dfb.order.Order;
import com.byakuya.boot.backend.component.dfb.order.OrderService;
import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.jackson.DynamicJsonView;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.byakuya.boot.backend.component.dfb.ConstantUtils.CUSTOMER_PREFIX;

/**
 * Created by 田伯光 at 2023/1/5 22:54
 */
@ApiModule(path = "dfb/customers")
@Validated
class CustomerController {
    private final CustomerService customerService;
    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;

    CustomerController(CustomerService customerService, OrderService orderService, RedisTemplate<String, Object> redisTemplate) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
    }


    @GetMapping(path = {"/me", "/{id}"})
    public Customer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        return customerService.query(id != null ? id : authentication.getAccountId(), true).orElseThrow(() -> AuthException.forbidden(null));
    }

    @AclApiMethod(value = "customer_list", desc = "顾客列表", method = RequestMethod.GET)
    public Page<CustomerWithOrderVO> read(@PageableDefault(sort = {"user.account.createTime"}, direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "phone", required = false) String phoneLike) {
        Page<Customer> customers = customerService.query(pageable, phoneLike);
        Map<Long, Order> lastOrderMap = orderService.getCustomerLastOrder(customers.stream().map(Customer::getId).collect(Collectors.toList()));
        return customers.map(customer -> new CustomerWithOrderVO(customer, lastOrderMap.get(customer.getId())));
    }

    @PostMapping("/call")
    @DynamicJsonView(type = Lawyer.class, include = {"name", "lawId"})
    public Lawyer read(AccountAuthentication authentication) {
        if (customerService.checkInvalid(authentication.getAccountId())) {
            return null;
        }
        String customerKey = CUSTOMER_PREFIX + authentication.getAccountId();
        String hTimeKey = "timestamp";
        String hOrderKey = "order";
        if (!redisTemplate.opsForHash().putIfAbsent(customerKey, hTimeKey, LocalDateTime.now())) {
            return ((Order) redisTemplate.opsForHash().get(customerKey, hOrderKey)).getLawyer();
        }
        try {
            String hExcludeKey = "exclude";
            String exclude = Optional.ofNullable(redisTemplate.opsForHash().get(customerKey, hExcludeKey)).map(Object::toString).orElse("");
            Optional<Order> orderOptional = orderService.create(authentication.getAccountId(), exclude);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                HashMap<String, Object> map = new HashMap<>();
                Lawyer lawyer = order.getLawyer();
                map.put(hOrderKey, order);
                map.put(hTimeKey, order.getUpdateTime());
                map.put(hExcludeKey, (StringUtils.hasText(exclude) ? (lawyer.getId() + "," + exclude) : String.valueOf(lawyer.getId())));
                redisTemplate.opsForHash().putAll(customerKey, map);
                return lawyer;
            } else {
                redisTemplate.delete(customerKey);
                return null;
            }
        } catch (Exception e) {
            redisTemplate.delete(customerKey);
            throw e;
        }
    }
}
