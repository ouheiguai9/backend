package com.byakuya.boot.backend.component.unique;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by 田伯光 at 2022/9/12 21:02
 */
interface UniqueRepository extends CrudRepository<Unique, Long> {
    Optional<Unique> findByTenant_IdAndUniqueTypeAndUniqueValue(Long tenantId, Type uniqueType, String uniqueValue);
    
    void deleteByTenant_IdAndUniqueTypeAndUniqueValue(Long tenantId, Type uniqueType, String uniqueValue);
}
