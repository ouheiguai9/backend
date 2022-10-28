package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.component.unique.Type;
import com.byakuya.boot.backend.component.unique.UniqueService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Created by 田伯光 at 2022/10/7 17:11
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UniqueService uniqueService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UniqueService uniqueService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.uniqueService = uniqueService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User save(User user) {
        if (StringUtils.hasText(user.getUsername())) {
            uniqueService.addUnique(user.getTenantId(), Type.USERNAME, user.getUsername());
        }
        if (StringUtils.hasText(user.getPhone())) {
            uniqueService.addUnique(user.getTenantId(), Type.PHONE, user.getPhone());
        }
        if (StringUtils.hasText(user.getEmail())) {
            uniqueService.addUnique(user.getTenantId(), Type.EMAIL, user.getEmail());
        }
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
}
