package com.byakuya.boot.backend.component.tenant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by 田伯光 at 2022/10/9 21:34
 */
interface TenantRepository extends JpaRepository<Tenant, Long> {
    List<Tenant> findAllByPrefixIsNotNull();
}
