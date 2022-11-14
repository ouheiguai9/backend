package com.byakuya.boot.backend.component.role;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 田伯光 at 2022/4/28 17:27
 */
interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByNameAndTenant_id(String name, Long tenantId);
}
