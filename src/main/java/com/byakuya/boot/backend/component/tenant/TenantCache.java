package com.byakuya.boot.backend.component.tenant;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Created by 相亲于盛夏 at 2023/2/5 22:36
 */
public class TenantCache<T> {
    private final ConcurrentHashMap<Long, T> cache = new ConcurrentHashMap<>();
    private final Function<Long, T> function;

    public TenantCache(Function<Long, T> function) {
        this.function = function;
    }

    public T get(Long tenantId) {
        return cache.computeIfAbsent(tenantId, function);
    }

    public T reset(Long tenantId) {
        return reset(tenantId, function.apply(tenantId));
    }

    public T reset(Long tenantId, T value) {
        return cache.put(tenantId, value);
    }

    public T remove(Long tenantId) {
        return cache.remove(tenantId);
    }
}
