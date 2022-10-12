package com.byakuya.boot.backend.component.user;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by 田伯光 at 2022/9/4 16:31
 */
interface UserRepository extends PagingAndSortingRepository<User, Long> {
}
