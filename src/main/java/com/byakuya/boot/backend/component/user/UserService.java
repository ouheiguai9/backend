package com.byakuya.boot.backend.component.user;

import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.component.unique.Type;
import com.byakuya.boot.backend.component.unique.UniqueService;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

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
    public User createByUsername(String username, String password, Long tenantId) {
        if (!StringUtils.hasText(password) || Objects.isNull(tenantId)) {
            throw new BackendException(ErrorStatus.INVALID_PARAMETER);
        }
        uniqueService.addUnique(tenantId, Type.USERNAME, username);
        return userRepository.save(initUser(password, tenantId).setUsername(username));
    }

    @Transactional
    public User createByPhone(String phone, String password, Long tenantId) {
        if (!StringUtils.hasText(password) || Objects.isNull(tenantId)) {
            throw new BackendException(ErrorStatus.INVALID_PARAMETER);
        }
        uniqueService.addUnique(tenantId, Type.PHONE, phone);
        return userRepository.save(initUser(password, tenantId).setPhone(phone));
    }

    private User initUser(String password, Long tenantId) {
        User user = new User();
        Account account = new Account();
        account.setTenantId(tenantId);
        user.setPassword(passwordEncoder.encode(password));
        user.setTenantId(tenantId);
        user.setAccount(account);
        return user;
    }

}
