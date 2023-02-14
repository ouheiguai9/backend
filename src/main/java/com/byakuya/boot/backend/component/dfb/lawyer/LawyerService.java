package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by 田伯光 at 2023/2/8 17:11
 */
@Service
public class LawyerService {

    private final LawyerRepository lawyerRepository;
    private final UserService userService;

    public LawyerService(LawyerRepository lawyerRepository, UserService userService) {
        this.lawyerRepository = lawyerRepository;
        this.userService = userService;
    }

    public List<Lawyer> queryAll() {
        return lawyerRepository.findAll();
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
                lawyer.setState(LawyerState.NOT_APPROVED);
                return Optional.of(lawyerRepository.save(lawyer));
            }
        }
        return Optional.empty();
    }

    @Transactional
    public void approve(Long accountId) {
        lawyerRepository.findById(accountId).ifPresent(lawyer -> {
            lawyer.setState(lawyer.getState().transition(LawyerAction.APPROVED));
            lawyerRepository.save(lawyer);
        });
    }

    @Transactional
    public Lawyer dutyOn(Long accountId) {
        return lawyerRepository.findById(accountId).map(lawyer -> {
            LawyerState src = lawyer.getState();
            LawyerState target = src.transition(LawyerAction.ON);
            if (target != src && target == LawyerState.ON_DUTY) {
                lawyer.setState(target);
                return lawyerRepository.save(lawyer);
            }
            return null;
        }).orElse(null);
    }

    @Transactional
    public Lawyer dutyOff(Long accountId) {
        return lawyerRepository.findById(accountId).filter(lawyer -> !Boolean.TRUE.equals(lawyer.getBackup())).map(lawyer -> {
            LawyerState src = lawyer.getState();
            LawyerState target = src.transition(LawyerAction.OFF);
            if (target != src && target == LawyerState.OFF_DUTY) {
                lawyer.setState(target);
                return lawyerRepository.save(lawyer);
            }
            return null;
        }).orElse(null);
    }

    public void beginWorking(Lawyer lawyer) {
        LawyerState next = lawyer.getState().transition(LawyerAction.START);
        if (next == lawyer.getState()) throw new RuntimeException("Lawyer state is error");
        lawyerRepository.save(lawyer);
    }

}
