package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.component.unique.Type;
import com.byakuya.boot.backend.component.unique.UniqueService;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

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
    public User add(User user) {
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
        Account account = new Account();
        account.setTenant(user.getTenant());
        //超级管理员创建用户为租户管理员
        if (Optional.ofNullable(SecurityContextHolder.getContext()).map(x -> AccountAuthentication.isAdmin(x.getAuthentication())).orElse(false)) {
            account.setAdmin(true);
        }
        user.setAccount(account);
        return userRepository.save(user);
    }

    public Optional<User> query(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> loadByUsername(String username, Long tenantId) {
        return userRepository.findByUsernameAndTenant_id(username, tenantId);
    }

    public Optional<User> loadByPhone(String phone, Long tenantId) {
        return userRepository.findByPhoneAndTenant_id(phone, tenantId);
    }

    public Optional<User> loadByEmail(String email, Long tenantId) {
        return userRepository.findByEmailAndTenant_id(email, tenantId);
    }
}
