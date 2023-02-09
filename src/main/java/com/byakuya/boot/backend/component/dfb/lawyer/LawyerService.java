package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.component.dfb.customer.Customer;
import com.byakuya.boot.backend.component.dfb.customer.CustomerService;
import com.byakuya.boot.backend.component.dfb.order.OrderService;
import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 田伯光 at 2023/2/8 17:11
 */
@Service
public class LawyerService implements InitializingBean {
    private static final String REDIS_PREFIX = "dfb:";
    private static final String LAWYER_PREFIX = REDIS_PREFIX + "lawyer:";
    private static final String INFO_KEY = LAWYER_PREFIX + "infos";
    private static final String CANDIDATES_KEY = LAWYER_PREFIX + "candidates";
    private static final String BACKUP_KEY = LAWYER_PREFIX + "backups";
    private static final String ORDER_PREFIX = REDIS_PREFIX + "order:";
    private final LawyerRepository lawyerRepository;
    private final UserService userService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final StringRedisTemplate stringRedisTemplate;

    public LawyerService(LawyerRepository lawyerRepository, UserService userService, CustomerService customerService, OrderService orderService, StringRedisTemplate stringRedisTemplate) {
        this.lawyerRepository = lawyerRepository;
        this.userService = userService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Transactional
    public String allocate(long customerId, String exclude) {
        String orderKey = ORDER_PREFIX + customerId;
        if (Boolean.FALSE.equals(stringRedisTemplate.opsForValue().setIfAbsent(orderKey, "", 1, TimeUnit.MINUTES))) {
            return stringRedisTemplate.opsForValue().get(orderKey) + ':' + exclude;
        }
        Optional<String> opt = Optional.ofNullable(stringRedisTemplate.opsForZSet().popMin(CANDIDATES_KEY)).map(ZSetOperations.TypedTuple::getValue).filter(x -> !exclude.contains(x));
        if (!opt.isPresent()) {
            opt = Optional.ofNullable(stringRedisTemplate.opsForZSet().popMin(BACKUP_KEY)).map(ZSetOperations.TypedTuple::getValue);
        }
        try {
            opt.ifPresent(lawyerId -> {
                Customer customer = customerService.query(customerId, false).orElseThrow(RecordNotFoundException::new);
                Lawyer lawyer = lawyerRepository.findById(customerId).orElseThrow(RecordNotFoundException::new);
            });
        } catch (Exception e) {

        }
        return "";
    }


    @Transactional
    public Optional<Lawyer> query(long accountId, boolean createIfNotExist) {
        Optional<Lawyer> rtnVal = lawyerRepository.findById(accountId);
        if (rtnVal.isPresent() || !createIfNotExist) return rtnVal;
        Optional<User> opt = userService.query(accountId);
        if (opt.isPresent()) {
            User user = opt.get();
            String phone = user.getPhone();
            if (StringUtils.hasText(phone) && user.getPhone().charAt(0) == 'L') {
                Lawyer lawyer = new Lawyer().setUser(user);
                lawyer.setPhone(phone.substring(1));
                lawyer.setState(LawyerState.NOT_APPROVED);
                return Optional.of(lawyerRepository.save(lawyer));
            }
        }
        return Optional.empty();
    }

    @Transactional
    public void approve(long accountId) {
        lawyerRepository.findById(accountId).ifPresent(lawyer -> {
            lawyer.setState(lawyer.getState().transition(LawyerAction.APPROVED));
            lawyerRepository.save(lawyer);
        });
    }

    @Transactional
    public void dutyOn(long accountId) {
        lawyerRepository.findById(accountId).ifPresent(lawyer -> {
            LawyerState src = lawyer.getState();
            LawyerState target = src.transition(LawyerAction.ON);
            if (target != src && target == LawyerState.ON_DUTY) {
                String id = String.valueOf(lawyer.getId());
                stringRedisTemplate.opsForHash().put(INFO_KEY, id, lawyer.getPhone());
                stringRedisTemplate.opsForZSet().addIfAbsent(CANDIDATES_KEY, id, System.currentTimeMillis());
                lawyer.setState(target);
                lawyerRepository.save(lawyer);
            }
        });
    }

    @Transactional
    public void dutyOff(long accountId) {
        lawyerRepository.findById(accountId).ifPresent(lawyer -> {
            LawyerState src = lawyer.getState();
            LawyerState target = src.transition(LawyerAction.OFF);
            if (target != src && target == LawyerState.OFF_DUTY) {
                stringRedisTemplate.opsForZSet().remove(CANDIDATES_KEY, String.valueOf(lawyer.getId()));
                lawyer.setState(target);
                lawyerRepository.save(lawyer);
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String initLockKey = REDIS_PREFIX + "init:lock";
        boolean initLock = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(initLockKey, "lock", 3, TimeUnit.MINUTES));
        if (!initLock) return;
        stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                try {
                    List<Lawyer> lawyerList = lawyerRepository.findAll();
                    if (!lawyerList.isEmpty()) {
                        AtomicLong min = new AtomicLong(System.currentTimeMillis());
                        lawyerList.forEach(lawyer -> {
                            String id = String.valueOf(lawyer.getId());
                            operations.opsForHash().put((K) INFO_KEY, id, lawyer.getPhone());
                            if (lawyer.getState() == LawyerState.ON_DUTY) {
                                operations.opsForZSet().addIfAbsent((K) (lawyer.isBackup() ? BACKUP_KEY : CANDIDATES_KEY), (V) id, min.getAndIncrement());
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
