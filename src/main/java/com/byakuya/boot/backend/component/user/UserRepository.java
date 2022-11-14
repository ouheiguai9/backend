package com.byakuya.boot.backend.component.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by 田伯光 at 2022/9/4 16:31
 */
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndTenant_id(String username, Long tenant_id);

    Optional<User> findByPhoneAndTenant_id(String phone, Long tenant_id);

    Optional<User> findByEmailAndTenant_id(String email, Long tenant_id);
}
