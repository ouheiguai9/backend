package com.byakuya.boot.backend.component.dfb.lawyer;

import com.byakuya.boot.backend.component.user.User;
import com.byakuya.boot.backend.component.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
}
