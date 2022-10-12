package com.byakuya.boot.backend.component.unique;

import org.springframework.stereotype.Service;

/**
 * Created by 田伯光 at 2022/10/11 21:27
 */
@Service
public class UniqueService {
    private final UniqueRepository uniqueRepository;

    public UniqueService(UniqueRepository uniqueRepository) {
        this.uniqueRepository = uniqueRepository;
    }

    public Unique addUnique(Long tenantId, Type uniqueType, String content) {
        return uniqueRepository.save(new Unique().setTenantId(tenantId).setUniqueType(uniqueType).setUniqueValue(content));
    }

    public void removeUnique(Long tenantId, Type uniqueType, String content) {
        uniqueRepository.deleteByTenant_IdAndUniqueTypeAndUniqueValue(tenantId, uniqueType, content);
    }

    public boolean checkUnique(Long tenantId, Type uniqueType, String content) {
        return !uniqueRepository.findByTenant_IdAndUniqueTypeAndUniqueValue(tenantId, uniqueType, content).isPresent();
    }
}
