package com.byakuya.boot.backend.component.unique;

import com.byakuya.boot.backend.exception.ValidationFailedException;
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
        if (exists(tenantId, uniqueType, content)) {
            throw ValidationFailedException.buildWithCode(uniqueType.errorCode);
        }
        return uniqueRepository.save(new Unique().setTenantId(tenantId).setUniqueType(uniqueType).setUniqueValue(content));
    }

    public boolean exists(Long tenantId, Type uniqueType, String content) {
        return uniqueRepository.existsById(new UniqueId().setTenantId(tenantId).setUniqueType(uniqueType).setUniqueValue(content));
    }

    public void removeUnique(Long tenantId, Type uniqueType, String content) {
        uniqueRepository.deleteById(new UniqueId().setTenantId(tenantId).setUniqueType(uniqueType).setUniqueValue(content));
    }
}
