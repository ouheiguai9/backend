package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by 田伯光 at 2023/2/8 17:11
 */
@Service
public class LawyerService implements InitializingBean {
    private static final String REDIS_PREFIX = "dfb:lawyer:";
    private static final String INFO_KEY = REDIS_PREFIX + "infos";
    private static final String CANDIDATES_KEY = REDIS_PREFIX + "candidates";
    private static final String WORKING_KEY = REDIS_PREFIX + "workings";
    private final LawyerRepository lawyerRepository;
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    public LawyerService(LawyerRepository lawyerRepository, UserService userService, StringRedisTemplate stringRedisTemplate) {
        this.lawyerRepository = lawyerRepository;
        this.userService = userService;
        this.stringRedisTemplate = stringRedisTemplate;
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
        try {
            List<Lawyer> lawyerList = lawyerRepository.findAll();
            lawyerList.forEach(lawyer -> {
                String id = String.valueOf(lawyer.getId());
                stringRedisTemplate.opsForHash().put(INFO_KEY, id, lawyer.getPhone());
                switch (lawyer.getState()) {
                    case WORKING:
                        stringRedisTemplate.opsForSet().add(WORKING_KEY, id);
                        break;
                    case ON_DUTY:
                        stringRedisTemplate.opsForZSet().addIfAbsent(CANDIDATES_KEY, id, System.currentTimeMillis());
                        break;
                }
            });
        } finally {
            stringRedisTemplate.delete(initLockKey);
        }
    }
}
