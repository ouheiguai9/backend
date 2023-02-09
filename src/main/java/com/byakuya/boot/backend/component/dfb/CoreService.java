package com.byakuya.boot.backend.component.dfb;

import com.byakuya.boot.backend.component.dfb.lawyer.Lawyer;
import com.byakuya.boot.backend.component.dfb.lawyer.LawyerService;
import com.byakuya.boot.backend.component.dfb.lawyer.LawyerState;
import com.byakuya.boot.backend.component.dfb.order.Order;
import com.byakuya.boot.backend.component.dfb.order.OrderService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 田伯光 at 2023/2/10 0:18
 */
@Service
public class CoreService implements InitializingBean {
    private static final String REDIS_PREFIX = "dfb:";
    private static final String LAWYER_PREFIX = REDIS_PREFIX + "lawyer:";
    private static final String CANDIDATES_KEY = LAWYER_PREFIX + "candidates";
    private static final String BACKUP_KEY = LAWYER_PREFIX + "backups";
    private static final String ORDER_PREFIX = REDIS_PREFIX + "order:";

    private final OrderService orderService;
    private final LawyerService lawyerService;
    private final StringRedisTemplate stringRedisTemplate;

    public CoreService(OrderService orderService, LawyerService lawyerService, StringRedisTemplate stringRedisTemplate) {
        this.orderService = orderService;
        this.lawyerService = lawyerService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String call(long customerId, String exclude) {
        String orderKey = ORDER_PREFIX + customerId;
        if (Boolean.FALSE.equals(stringRedisTemplate.opsForValue().setIfAbsent(orderKey, "", 1, TimeUnit.MINUTES))) {
            return stringRedisTemplate.opsForValue().get(orderKey) + ':' + exclude;
        }
        Optional<String> opt = Optional.ofNullable(stringRedisTemplate.opsForZSet().popMin(CANDIDATES_KEY)).map(ZSetOperations.TypedTuple::getValue).filter(x -> !exclude.contains(x));
        if (!opt.isPresent()) {
            opt = Optional.ofNullable(stringRedisTemplate.opsForZSet().popMin(BACKUP_KEY)).map(ZSetOperations.TypedTuple::getValue);
        }
        try {
            return opt.map(lawyerId -> {
                Order order = orderService.create(customerId, Long.valueOf(lawyerId));
                stringRedisTemplate.opsForValue().set(orderKey, String.valueOf(order.getSerial()), 95, TimeUnit.MINUTES);
                return order.getSerial() + ":" + (StringUtils.hasText(exclude) ? (lawyerId + "," + exclude) : lawyerId);
            }).orElse("");
        } catch (Exception e) {
            stringRedisTemplate.delete(orderKey);
            throw e;
        }
    }

    public void addCandidateLawyer(Lawyer lawyer) {
        if (lawyer == null) return;
        stringRedisTemplate.opsForZSet().addIfAbsent(lawyer.isBackup() ? BACKUP_KEY : CANDIDATES_KEY, String.valueOf(lawyer.getId()), System.currentTimeMillis());
    }

    public void removeCandidateLawyer(Lawyer lawyer) {
        if (lawyer == null) return;
        stringRedisTemplate.opsForZSet().remove(lawyer.isBackup() ? BACKUP_KEY : CANDIDATES_KEY, String.valueOf(lawyer.getId()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String initLockKey = REDIS_PREFIX + "init:lock";
        boolean initLock = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(initLockKey, "lock", 3, TimeUnit.MINUTES));
        if (!initLock) return;
        stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public <K, V> Object execute(@NotNull RedisOperations<K, V> operations) throws DataAccessException {
                try {
                    List<Lawyer> lawyerList = lawyerService.queryAll();
                    if (!lawyerList.isEmpty()) {
                        AtomicLong min = new AtomicLong(System.currentTimeMillis());
                        lawyerList.forEach(lawyer -> {
                            if (lawyer.getState() == LawyerState.ON_DUTY) {
                                operations.opsForZSet().addIfAbsent((K) (lawyer.isBackup() ? BACKUP_KEY : CANDIDATES_KEY), (V) String.valueOf(lawyer.getId()), min.getAndIncrement());
                            }
                        });
                    }
                    return null;
                } finally {
                    operations.delete((K) initLockKey);
                }
            }
        });
    }
}
