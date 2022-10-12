package com.byakuya.boot.backend.component.account;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 田伯光 at 2022/4/28 17:27
 */
interface AccountRepository extends JpaRepository<Account, Long> {
}
