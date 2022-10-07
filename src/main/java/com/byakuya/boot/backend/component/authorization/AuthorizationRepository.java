package com.byakuya.boot.backend.component.authorization;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by 田伯光 at 2022/10/5 11:39
 */
public interface AuthorizationRepository extends PagingAndSortingRepository<Authorization, Long> {
}
