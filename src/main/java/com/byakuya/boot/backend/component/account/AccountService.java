package com.byakuya.boot.backend.component.account;

import com.byakuya.boot.backend.component.authorization.AuthorizationService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by 田伯光 at 2022/8/28 22:02
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuthorizationService authorizationService;

    public AccountService(AccountRepository accountRepository, AuthorizationService authorizationService) {
        this.accountRepository = accountRepository;
        this.authorizationService = authorizationService;
    }

    public Optional<Account> query(long id) {
        return accountRepository.findById(id);
    }

    public Set<String> getAccountApiAuth(long id) {
        Set<Long> idSet = new HashSet<>();
        idSet.add(id);
        accountRepository.findById(id).ifPresent(account -> account.getRoles().forEach(role -> idSet.add(role.getId())));
        return authorizationService.queryApiAuth(idSet);
    }
}
