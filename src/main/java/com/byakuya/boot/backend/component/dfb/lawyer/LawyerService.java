package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.component.account.AccountService;
import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final AccountService accountService;

    public LawyerService(LawyerRepository lawyerRepository, StringRedisTemplate stringRedisTemplate, UserService userService, AccountService accountService) {
        this.lawyerRepository = lawyerRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.userService = userService;
        this.accountService = accountService;
    }

    public List<Lawyer> queryAllStat(LocalDateTime start, LocalDateTime end) {
        return lawyerRepository.findAllWithOrder(start, end);
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
        LawyerState state = old.getState();
        if (state != LawyerState.CREATED) return old;
        old.setName(lawyer.getName());
        old.setCertificate(lawyer.getCertificate());
        old.setLawId(lawyer.getLawId());
        old.setLawFirm(lawyer.getLawFirm());
        old.setKey1(lawyer.getKey1());
        old.setKey2(lawyer.getKey2());
        old.setKey3(lawyer.getKey3());
        old.setKey4(lawyer.getKey4());
        old.setKey5(lawyer.getKey5());
        old.setKey6(lawyer.getKey6());
        old.setKey7(lawyer.getKey7());
        old.setKey8(lawyer.getKey8());
        old.setKey9(lawyer.getKey9());
        old.setBackup(Boolean.FALSE);
        old.setState(state.transition(LawyerAction.SUBMIT));
        return lawyerRepository.save(old);
    }

    @Transactional
    public Optional<Lawyer> approve(Long lawyerId, LawyerAction action) {
        return lawyerRepository.findById(lawyerId).map(lawyer -> {
            lawyer.setState(lawyer.getState().transition(action));
            return lawyerRepository.save(lawyer);
        });
    }

    @Transactional
    public void setLocked(Long lawyerId, Boolean locked) {
        lawyerRepository.findWithUser(lawyerId).ifPresent(lawyer -> {
            if (lawyer.getUser().getAccount().isLocked() != locked) {
                if (locked && (lawyer.getBackup() || lawyer.getState() == LawyerState.NOT_APPROVED || lawyer.getState() == LawyerState.CREATED)) {
                    throw AuthException.forbidden(null);
                }
                if (locked) {
                    stringRedisTemplate.opsForValue().setIfAbsent(LOCKED_LAWYER_PREFIX_KEY + lawyerId, "1");
                } else {
                    stringRedisTemplate.delete(LOCKED_LAWYER_PREFIX_KEY + lawyerId);
                }
                accountService.setLocked(lawyerId, locked);
            }
        });
    }

    @Transactional
    public void setBackup(Long lawyerId, Boolean backup) {
        lawyerRepository.findWithUser(lawyerId).ifPresent(lawyer -> {
            if (!lawyer.getBackup().equals(backup)) {
                if (backup && (Boolean.TRUE.equals(lawyer.getUser().getAccount().isLocked()) || lawyer.getState() == LawyerState.NOT_APPROVED || lawyer.getState() == LawyerState.CREATED)) {
                    throw AuthException.forbidden(null);
                }
                lawyerRepository.save(lawyer.setBackup(backup));
            }
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
            do {
                curr = Optional.ofNullable(stringRedisTemplate.opsForZSet().popMin(CANDIDATES_KEY));
            } while (curr.filter(tuple -> StringUtils.hasText(stringRedisTemplate.opsForValue().getAndDelete(LOCKED_LAWYER_PREFIX_KEY + tuple.getValue()))).isPresent());
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
                List<Lawyer> lawyerList = lawyerRepository.findAllUnLocked();
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

    public Page<LawyerFullVO> query(Pageable pageable, String nameLike, String phoneLike, LawyerState[] stateIn, String[] keyIn, LocalDateTime[] createTimeIn) {
        return lawyerRepository.findAll((Specification<Lawyer>) (root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (StringUtils.hasText(nameLike)) {
                conditions.add(builder.like(root.get("name"), "%" + nameLike + "%"));
            }
            if (StringUtils.hasText(phoneLike)) {
                conditions.add(builder.like(root.get("phone"), "%" + phoneLike + "%"));
            }
            if (stateIn != null && stateIn.length > 0) {
                conditions.add(root.get("state").in(stateIn));
            }
            if (keyIn != null) {
                for (String key : keyIn) {
                    conditions.add(builder.equal(root.get(key), true));
                }
            }
            if (createTimeIn != null && createTimeIn.length == 2) {
                conditions.add(builder.between(root.get("user.createTime"), createTimeIn[0], createTimeIn[1]));
            }
            return query.where(conditions.toArray(conditions.toArray(new Predicate[0]))).getRestriction();
        }, pageable).map(LawyerFullVO::new);
    }
}
