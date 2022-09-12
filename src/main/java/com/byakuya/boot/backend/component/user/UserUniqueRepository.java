package com.byakuya.boot.backend.component.user;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by 田伯光 at 2022/9/12 21:02
 */
interface UserUniqueRepository extends CrudRepository<UserUnique, Long> {
}
