package com.byakuya.boot.backend.component.dfb.customer;

import com.byakuya.boot.backend.component.dfb.lawyer.Lawyer;
import com.byakuya.boot.backend.component.dfb.order.Evaluation;
import com.byakuya.boot.backend.component.dfb.order.Order;
import com.byakuya.boot.backend.component.dfb.order.OrderService;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.jackson.DynamicJsonView;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

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


    @GetMapping(path = {"", "/{id}"})
    public Customer read(@PathVariable(required = false) Long id, AccountAuthentication authentication) {
        return customerService.query(id != null ? id : authentication.getAccountId(), true).orElseThrow(() -> AuthException.forbidden(null));
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

    @PostMapping("/evaluation")
    public Evaluation evaluation(@RequestBody Evaluation evaluation, AccountAuthentication authentication) {
        if (evaluation.getOrderId() == null && !authentication.isTenantAdmin()) {
            //虚拟评价只能租户管理员能添加
            throw AuthException.forbidden(null);
        }
        return orderService.addEvaluation(evaluation, authentication.getAccountId());
    }

    @GetMapping("/orders")
    public Page<Order> read(@PageableDefault Pageable pageable, String search) {
        return null;
    }
}
