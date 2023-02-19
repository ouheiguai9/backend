package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
import org.jetbrains.annotations.NotNull;
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

import static com.byakuya.boot.backend.component.dfb.ConstantUtils.*;

/**
 * Created by 田伯光 at 2023/2/8 17:11
 */
@Service
public class LawyerService implements InitializingBean {

    private final LawyerRepository lawyerRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserService userService;

    public LawyerService(LawyerRepository lawyerRepository, StringRedisTemplate stringRedisTemplate, UserService userService) {
        this.lawyerRepository = lawyerRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.userService = userService;
    }

    @Transactional
    public Optional<Lawyer> query(Long accountId, boolean createIfNotExist) {
        Optional<Lawyer> rtnVal = lawyerRepository.findById(accountId);
        if (rtnVal.isPresent() || !createIfNotExist) return rtnVal;
        Optional<User> opt = userService.query(accountId);
        if (opt.isPresent()) {
            User user = opt.get();
            String phone = user.getPhone();
            if (StringUtils.hasText(phone) && user.getPhone().charAt(0) == 'L') {
                Lawyer lawyer = new Lawyer().setUser(user);
                lawyer.setPhone(phone.substring(1));
                lawyer.setState(LawyerState.CREATED);
                return Optional.of(lawyerRepository.save(lawyer));
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Lawyer submitInfo(Lawyer lawyer) {
        Lawyer old = lawyerRepository.findById(lawyer.getId()).orElseThrow(RecordNotFoundException::new);
        LawyerState state = lawyer.getState();
        if (state != LawyerState.CREATED) return old;
        lawyer.setName(lawyer.getName());
        lawyer.setCertificate(lawyer.getCertificate());
        lawyer.setLawId(lawyer.getLawId());
        lawyer.setLawFirm(lawyer.getLawFirm());
        lawyer.setKey1(lawyer.getKey1());
        lawyer.setKey1(lawyer.getKey2());
        lawyer.setKey1(lawyer.getKey3());
        lawyer.setKey1(lawyer.getKey4());
        lawyer.setKey1(lawyer.getKey5());
        lawyer.setKey1(lawyer.getKey6());
        lawyer.setKey1(lawyer.getKey7());
        lawyer.setKey1(lawyer.getKey8());
        lawyer.setKey1(lawyer.getKey9());
        lawyer.setBackup(Boolean.FALSE);
        lawyer.setState(state.transition(LawyerAction.SUBMIT));
        return lawyerRepository.save(old);
    }

    @Transactional
    public void approve(Long accountId) {
        lawyerRepository.findById(accountId).ifPresent(lawyer -> {
            lawyer.setState(lawyer.getState().transition(LawyerAction.APPROVED));
            lawyerRepository.save(lawyer);
        });
    }

    @Transactional
    public void dutyOn(Long lawyerId) {
        lawyerRepository.findById(lawyerId).ifPresent(lawyer -> {
            LawyerState src = lawyer.getState();
            LawyerState target = src.transition(LawyerAction.ON);
            if (target != src && target == LawyerState.ON_DUTY) {
                lawyer.setState(target);
                stringRedisTemplate.opsForZSet().addIfAbsent(candidateKey(lawyerRepository.save(lawyer)), String.valueOf(lawyerId), System.currentTimeMillis());
            }
        });
    }

    private String candidateKey(Lawyer lawyer) {
        return isBackup(lawyer) ? BACKUP_KEY : CANDIDATES_KEY;
    }

    private boolean isBackup(Lawyer lawyer) {
        return Boolean.TRUE.equals(lawyer.getBackup());
    }

    @Transactional
    public void dutyOff(Long lawyerId) {
        lawyerRepository.findById(lawyerId).filter(lawyer -> !isBackup(lawyer)).ifPresent(lawyer -> {
            LawyerState src = lawyer.getState();
            LawyerState target = src.transition(LawyerAction.OFF);
            if (target != src && target == LawyerState.OFF_DUTY) {
                lawyer.setState(target);
                stringRedisTemplate.opsForZSet().remove(candidateKey(lawyerRepository.save(lawyer)), String.valueOf(lawyerId));
            }
        });
    }

    @Transactional
    public void beginWorking(Lawyer lawyer) {
        LawyerState next = lawyer.getState().transition(LawyerAction.START);
        if (next == lawyer.getState()) throw new RuntimeException("Lawyer state is error");
        stringRedisTemplate.opsForZSet().remove(candidateKey(lawyerRepository.save(lawyer.setState(next))), String.valueOf(lawyer.getId()));
    }

    @SuppressWarnings("ConstantConditions")
    @Transactional
    public Optional<Lawyer> election(String excludeLawyer) {
        Optional<ZSetOperations.TypedTuple<String>> curr = Optional.empty(), prev;
        do {
            prev = curr;
            curr = Optional.ofNullable(stringRedisTemplate.opsForZSet().popMin(CANDIDATES_KEY));
            prev.ifPresent(tuple -> stringRedisTemplate.opsForZSet().add(CANDIDATES_KEY, tuple.getValue(), tuple.getScore()));
        } while (curr.isPresent() && excludeLawyer.contains(curr.get().getValue()));
        if (!curr.isPresent()) {
            curr = Optional.ofNullable(stringRedisTemplate.opsForZSet().popMin(BACKUP_KEY));
        }
        if (!curr.isPresent()) {
            loadCandidates();
        }
        return curr.flatMap(tuple -> lawyerRepository.findById(Long.valueOf(tuple.getValue())).filter(x -> x.getState() == LawyerState.ON_DUTY));
    }

    private void loadCandidates() {
        boolean initLock = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(LAWYER_PREFIX + "init", "lock", 5, TimeUnit.SECONDS));
        if (!initLock) return;
        stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public <K, V> Object execute(@NotNull RedisOperations<K, V> operations) throws DataAccessException {
                List<Lawyer> lawyerList = lawyerRepository.findAll();
                if (!lawyerList.isEmpty()) {
                    AtomicLong min = new AtomicLong(System.currentTimeMillis());
                    lawyerList.forEach(lawyer -> {
                        if (lawyer.getState() == LawyerState.ON_DUTY) {
                            operations.opsForZSet().addIfAbsent((K) candidateKey(lawyer), (V) String.valueOf(lawyer.getId()), min.getAndIncrement());
                        }
                    });
                }
                return null;
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadCandidates();
    }
}
