package com.byakuya.boot.backend.component.account;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by 田伯光 at 2022/8/28 22:02
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> query(long id) {
        return accountRepository.findById(id);
    }
}
