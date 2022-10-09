package com.byakuya.boot.backend.component.tenant;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by 田伯光 at 2022/10/9 21:34
 */
public interface TenantRepository extends PagingAndSortingRepository<Tenant, Long> {
}
