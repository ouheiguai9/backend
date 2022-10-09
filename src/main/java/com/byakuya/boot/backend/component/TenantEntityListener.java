package com.byakuya.boot.backend.component;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Created by 田伯光 at 2022/10/9 19:05
 */
@Slf4j
public class TenantEntityListener {
    @PrePersist
    public void prePersist(Object target) {
        log.warn("PrePersist");
    }

    @PreUpdate
    public void preUpdate(Object target) {
        log.warn("PreUpdate");
    }
}
