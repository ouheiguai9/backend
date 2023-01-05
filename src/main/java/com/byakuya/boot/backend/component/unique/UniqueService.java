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
        Unique unique = new Unique(uniqueType, tenantId, content);
        //noinspection ConstantConditions
        if (uniqueRepository.existsById(unique.getId())) {
            throw ValidationFailedException.buildWithCode(uniqueType.errorCode);
        }
        unique.setNew(true);
        return uniqueRepository.save(unique);
    }

    public void removeUnique(Long tenantId, Type uniqueType, String content) {
        uniqueRepository.deleteById(new UniqueId(uniqueType, tenantId, content));
    }
}
