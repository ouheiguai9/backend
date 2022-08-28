package com.byakuya.boot.backend.component.account;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by 田伯光 at 2022/4/28 17:27
 */
public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {
}
